package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.dtos.request.booking.BookingMemberRequest;
import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateTourBookingRequest;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingSummaryResponse;
import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.finance.RefundPolicyRule;
import com.travery.traverybackend.entities.finance.RefundRequest;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.mappers.TourBookingMapper;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.finance.PaymentTransactionRepository;
import com.travery.traverybackend.repositories.finance.RefundRequestRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.booking.PaymentService;
import com.travery.traverybackend.services.booking.TourBookingService;
import com.travery.traverybackend.services.common.ChatSessionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourBookingServiceImpl implements TourBookingService {

  private static final int MIN_DAYS_BEFORE_DEPARTURE = 5;
  private static final int PAYMENT_DEADLINE_MINUTES = 15;
  private static final int CHILD_AGE_THRESHOLD = 11;

  private final TourInstanceRepository tourInstanceRepository;
  private final TourBookingRepository tourBookingRepository;
  private final BookingMemberRepository bookingMemberRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final UserRepository userRepository;
  private final ChatSessionService chatSessionService;
  private final TourBookingMapper tourBookingMapper;

  private final PaymentService paymentService;

  @Override
  @Transactional
  public TourBookingResponse createBooking(
      UUID instanceId, CreateTourBookingRequest request, UUID userId) {

    // 1. Load TourInstance with PESSIMISTIC_WRITE lock (prevents race condition)
    TourInstance instance =
        tourInstanceRepository
            .findByIdWithLock(instanceId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.TOUR_INSTANCE_NOT_FOUND));

    // 2. Validate instance is OPEN for booking
    if (instance.getStatus() != TourInstanceStatus.OPEN) {
      throw new BaseAppException(BookingErrorCode.TOUR_INSTANCE_NOT_OPEN);
    }

    Tour tour = instance.getTour();

    // 3. Validate departure is at least 5 days away
    long daysUntilDeparture = ChronoUnit.DAYS.between(LocalDate.now(), instance.getStartDate());
    if (daysUntilDeparture < MIN_DAYS_BEFORE_DEPARTURE) {
      throw new BaseAppException(BookingErrorCode.DEPARTURE_TOO_SOON);
    }

    // 4. Check available seats
    int requestedSeats = request.getMembers().size();
    int availableSlots = tour.getMaxParticipants() - instance.getCurrentParticipants();
    if (requestedSeats > availableSlots) {
      throw new BaseAppException(BookingErrorCode.NOT_ENOUGH_SEATS);
    }

    // 6. Load user reference
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_ACCESS_DENIED));

    // 5. Calculate total price (adult vs child based on dateOfBirth)
    BigDecimal totalPrice = calculateTotalPrice(request.getMembers(), tour);

    // 6. Create TourBooking
    LocalDateTime paymentDeadline = LocalDateTime.now().plusMinutes(PAYMENT_DEADLINE_MINUTES);
    TourBooking booking =
        TourBooking.builder()
            .user(user)
            .tourInstance(instance)
            .totalPrice(totalPrice)
            .pricePerAdultAtBooking(tour.getPricePerAdult())
            .pricePerChildAtBooking(tour.getPricePerChild())
            .paymentDeadline(paymentDeadline)
            .specialRequests(request.getSpecialRequests())
            .build();
    booking = tourBookingRepository.save(booking);

    // 7. Create BookingMember records using mapper (polymorphic: bookingId +
    // bookingType)
    List<BookingMember> members = createBookingMembers(request.getMembers(), booking.getId());
    members = new ArrayList<>(bookingMemberRepository.saveAll(members));

    // 8. Update currentParticipants on TourInstance; set FULL if needed
    int newParticipants = instance.getCurrentParticipants() + requestedSeats;
    instance.setCurrentParticipants(newParticipants);
    if (newParticipants >= tour.getMaxParticipants()) {
      instance.setStatus(TourInstanceStatus.FULL);
    }
    tourInstanceRepository.save(instance);

    // 9. Initiate VNPAY payment
    var paymentRequest =
        InitiatePaymentRequest.builder()
            .bookingId(booking.getId())
            .amount(booking.getTotalPrice())
            .ipAddress(request.getIpAddress())
            .build();

    var paymentResponse =
        paymentService.initiatePayment(
            booking.getId(), paymentRequest, userId, BookingType.TOUR_BOOKING);

    log.info("Booking {} created with payment deadline at {}", booking.getId(), paymentDeadline);

    // 10. Map to response using mapper
    return tourBookingMapper.toTourBookingResponse(booking, members, paymentResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TourBookingSummaryResponse> getMyBookings(
      UUID userId, BookingStatus status, Pageable pageable) {

    Page<TourBooking> bookingPage;
    if (status != null) {
      bookingPage = tourBookingRepository.findByUser_IdAndStatus(userId, status, pageable);
    } else {
      bookingPage = tourBookingRepository.findByUser_Id(userId, pageable);
    }

    // Batch fetch member counts in 1 query instead of N+1
    List<UUID> bookingIds = bookingPage.getContent().stream().map(TourBooking::getId).toList();

    Map<UUID, Integer> memberCountMap = new HashMap<>();
    if (!bookingIds.isEmpty()) {
      bookingMemberRepository
          .countByBookingIds(bookingIds, BookingType.TOUR_BOOKING)
          .forEach(row -> memberCountMap.put((UUID) row[0], ((Long) row[1]).intValue()));
    }

    return bookingPage.map(
        booking -> {
          int memberCount = memberCountMap.getOrDefault(booking.getId(), 0);
          return tourBookingMapper.toTourBookingSummaryResponse(booking, memberCount);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public TourBookingDetailResponse getBookingDetail(UUID bookingId, UUID userId) {
    TourBooking booking =
        tourBookingRepository
            .findByIdAndUser_Id(bookingId, userId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

    List<BookingMember> members =
        bookingMemberRepository.findAllByBookingIdAndBookingType(
            booking.getId(), BookingType.TOUR_BOOKING);

    var payment =
        paymentTransactionRepository
            .findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
                booking.getId(), BookingType.TOUR_BOOKING)
            .orElse(null);

    return tourBookingMapper.toTourBookingDetailResponse(booking, members, payment);
  }

  @Override
  @Transactional
  public CancelBookingResponse cancelBooking(
      UUID bookingId, CancelBookingRequest request, UUID userId) {

    // 1. Load booking with tour details (need RefundPolicy)
    TourBooking booking =
        tourBookingRepository
            .findByIdWithDetails(bookingId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

    // 2. Verify ownership
    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(BookingErrorCode.BOOKING_ACCESS_DENIED);
    }

    // 3. Validate cancellable state
    BookingStatus currentStatus = booking.getStatus();
    if (currentStatus == BookingStatus.CANCELLED) {
      throw new BaseAppException(BookingErrorCode.BOOKING_ALREADY_CANCELLED);
    }

    TourInstance instance = booking.getTourInstance();
    if (instance.getStatus() == TourInstanceStatus.IN_PROGRESS) {
      throw new BaseAppException(BookingErrorCode.CANNOT_CANCEL_IN_PROGRESS);
    }
    if (instance.getStatus() == TourInstanceStatus.COMPLETED) {
      throw new BaseAppException(BookingErrorCode.CANNOT_CANCEL_COMPLETED);
    }

    // 4. Cancel booking & release seats (lock instance to prevent race condition)
    booking.setStatus(BookingStatus.CANCELLED);
    tourBookingRepository.save(booking);

    // Remove user from tour chat group
    try {
      chatSessionService.removeUserFromChat(
          booking.getTourInstance().getId(), booking.getUser().getId());
    } catch (Exception e) {
      log.error(
          "Failed to remove user {} from tour chat on cancellation", booking.getUser().getId(), e);
    }

    int memberCount =
        bookingMemberRepository.countByBookingIdAndBookingType(
            booking.getId(), BookingType.TOUR_BOOKING);

    // Re-load instance with PESSIMISTIC_WRITE lock for safe participant update
    TourInstance lockedInstance =
        tourInstanceRepository
            .findByIdWithLock(instance.getId())
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.TOUR_INSTANCE_NOT_FOUND));
    releaseSeats(lockedInstance, memberCount);

    // 6. Handle refund if booking was PAID
    if (currentStatus == BookingStatus.PAID) {
      return processRefund(booking, lockedInstance, request);
    }

    // PENDING booking — no payment was made, no refund needed
    return CancelBookingResponse.builder()
        .bookingId(booking.getId())
        .bookingStatus(BookingStatus.CANCELLED)
        .refundMessage("Booking cancelled. No payment was made.")
        .build();
  }

  private void releaseSeats(TourInstance instance, int seatCount) {
    int updated = Math.max(0, instance.getCurrentParticipants() - seatCount);
    instance.setCurrentParticipants(updated);
    if (instance.getStatus() == TourInstanceStatus.FULL) {
      instance.setStatus(TourInstanceStatus.OPEN);
      log.info("TourInstance {} transitioned FULL → OPEN", instance.getId());
    }
  }

  private CancelBookingResponse processRefund(
      TourBooking booking, TourInstance instance, CancelBookingRequest request) {

    PaymentTransaction payment =
        paymentTransactionRepository
            .findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
                booking.getId(), BookingType.TOUR_BOOKING)
            .orElse(null);

    if (payment == null) {
      return CancelBookingResponse.builder()
          .bookingId(booking.getId())
          .bookingStatus(BookingStatus.CANCELLED)
          .refundMessage("Booking cancelled. No payment transaction found.")
          .build();
    }

    // Calculate refund percentage from RefundPolicy
    long daysBeforeDeparture = ChronoUnit.DAYS.between(LocalDate.now(), instance.getStartDate());
    BigDecimal refundPct =
        calculateRefundPercentage(
            booking.getTourInstance().getTour().getRefundPolicy(), daysBeforeDeparture);

    BigDecimal refundAmount =
        booking
            .getTotalPrice()
            .multiply(refundPct)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    // Create RefundRequest (Coordinator approval is a future feature)
    RefundRequest refundRequest =
        RefundRequest.builder()
            .paymentTransaction(payment)
            .user(booking.getUser())
            .requestedAmount(refundAmount)
            .customerReason(request != null ? request.getReason() : null)
            .build();
    refundRequestRepository.save(refundRequest);

    log.info(
        "Refund request created for booking {}: {}% = {} VND",
        booking.getId(), refundPct, refundAmount);

    return CancelBookingResponse.builder()
        .bookingId(booking.getId())
        .bookingStatus(BookingStatus.CANCELLED)
        .refundAmount(refundAmount)
        .refundPercentage(refundPct)
        .refundStatus(refundRequest.getStatus())
        .refundMessage(
            String.format(
                "Refund request submitted: %s%% (%s VND). Awaiting coordinator approval.",
                refundPct.stripTrailingZeros().toPlainString(),
                refundAmount.stripTrailingZeros().toPlainString()))
        .build();
  }

  /**
   * Find the matching refund rule based on days before departure. Rules are ordered by daysBefore
   * DESC — pick the first rule where daysBeforeDeparture >= rule.daysBefore. If no rule matches,
   * refund is 0%.
   */
  private BigDecimal calculateRefundPercentage(RefundPolicy policy, long daysBeforeDeparture) {
    if (policy == null || policy.getRules() == null || policy.getRules().isEmpty()) {
      return BigDecimal.ZERO;
    }

    return policy.getRules().stream()
        .filter(rule -> daysBeforeDeparture >= rule.getDaysBefore())
        .max(Comparator.comparingInt(RefundPolicyRule::getDaysBefore))
        .map(RefundPolicyRule::getRefundPercentage)
        .orElse(BigDecimal.ZERO);
  }

  private BigDecimal calculateTotalPrice(List<BookingMemberRequest> members, Tour tour) {
    long adultCount =
        members.stream()
            .filter(m -> calculateAge(m.getDateOfBirth()) > CHILD_AGE_THRESHOLD)
            .count();
    long childCount = members.size() - adultCount;

    BigDecimal adultTotal = tour.getPricePerAdult().multiply(BigDecimal.valueOf(adultCount));
    BigDecimal childTotal = tour.getPricePerChild().multiply(BigDecimal.valueOf(childCount));

    return adultTotal.add(childTotal);
  }

  private int calculateAge(LocalDate dateOfBirth) {
    return Period.between(dateOfBirth, LocalDate.now()).getYears();
  }

  private List<BookingMember> createBookingMembers(
      List<BookingMemberRequest> memberRequests, UUID bookingId) {
    return memberRequests.stream()
        .<BookingMember>map(
            req -> {
              BookingMember member = tourBookingMapper.toBookingMember(req);
              member.setBookingId(bookingId);
              member.setBookingType(BookingType.TOUR_BOOKING);
              return member;
            })
        .toList();
  }
}
