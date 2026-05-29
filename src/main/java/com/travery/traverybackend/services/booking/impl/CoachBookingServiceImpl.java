package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.request.coach.CreateCoachBookingRequest;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.dtos.response.coach.CoachBookingDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachBookingResponse;
import com.travery.traverybackend.dtos.response.coach.CoachBookingSummaryResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.booking.CoachBookingSeat;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.finance.RefundPolicyRule;
import com.travery.traverybackend.entities.finance.RefundRequest;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.finance.RefundTimeUnit;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.exception.error.UserErrorCode;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingSeatRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.repositories.finance.PaymentTransactionRepository;
import com.travery.traverybackend.repositories.finance.RefundRequestRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.booking.CoachBookingService;
import com.travery.traverybackend.services.booking.PaymentService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoachBookingServiceImpl implements CoachBookingService {

  private final CoachTripRepository coachTripRepository;
  private final CoachBookingRepository coachBookingRepository;
  private final CoachBookingSeatRepository coachBookingSeatRepository;
  private final UserRepository userRepository;
  private final PaymentService paymentService;
  private final CoachMapper coachMapper;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final RefundRequestRepository refundRequestRepository;

  @Override
  @Transactional
  public CoachBookingResponse createBooking(
      CreateCoachBookingRequest request, UUID userId, String ipAddress) {

    // 1. Validate User
    var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    // 2. Load Trip with PESSIMISTIC_WRITE lock to prevent double-booking
    CoachTrip trip =
        coachTripRepository
            .findByIdForUpdate(request.getTripId())
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.COACH_TRIP_NOT_FOUND));

    if (trip.getStatus() != CoachTripStatus.OPEN) {
      throw new BaseAppException(BookingErrorCode.COACH_TRIP_NOT_OPEN);
    }

    if (trip.getDepartureTime().isBefore(LocalDateTime.now())) {
      throw new BaseAppException(BookingErrorCode.COACH_TRIP_NOT_OPEN);
    }

    // 3. Load requested seats
    List<SeatLayoutItem> requestedSeats = new ArrayList<>();
    
    // Validate for duplicate seat IDs in the request
    long uniqueSeatCount = request.getSeatLayoutItemIds().stream().distinct().count();
    if (uniqueSeatCount != request.getSeatLayoutItemIds().size()) {
      throw new BaseAppException(BookingErrorCode.INVALID_SEAT_LAYOUT);
    }

    if (trip.getCoach() == null || trip.getCoach().getSeatLayout() == null) {
      throw new BaseAppException(BookingErrorCode.INVALID_SEAT_LAYOUT);
    }
    
    Map<UUID, SeatLayoutItem> layoutSeatMap =
        trip.getCoach().getSeatLayout().getItems().stream()
            .collect(Collectors.toMap(SeatLayoutItem::getId, item -> item));

    for (UUID requestedSeatId : request.getSeatLayoutItemIds()) {
      SeatLayoutItem seat = layoutSeatMap.get(requestedSeatId);
      if (seat == null) {
        throw new BaseAppException(BookingErrorCode.INVALID_SEAT_LAYOUT);
      }
      requestedSeats.add(seat);
    }

    // 4. Check seat availability
    List<BookingStatus> excludedStatuses = List.of(BookingStatus.CANCELLED, BookingStatus.NO_SHOW);
    List<CoachBookingSeat> existingBookedSeats =
        coachBookingSeatRepository.findByTripIdAndBookingStatusNotIn(
            trip.getId(), excludedStatuses);

    Set<UUID> bookedSeatIds =
        existingBookedSeats.stream()
            .map(bs -> bs.getSeatLayoutItem().getId())
            .collect(Collectors.toSet());

    for (SeatLayoutItem seat : requestedSeats) {
      if (bookedSeatIds.contains(seat.getId())) {
        throw new BaseAppException(BookingErrorCode.SEAT_ALREADY_BOOKED);
      }
    }

    // 5. Check if trip becomes FULL after this booking
    int totalSeats = trip.getCoach().getSeatLayout().getTotalSeats();
    int newlyBookedCount = existingBookedSeats.size() + requestedSeats.size();
    if (newlyBookedCount >= totalSeats) {
      trip.setStatus(CoachTripStatus.FULL);
      coachTripRepository.save(trip);
    }

    // 6. Calculate Price
    BigDecimal basePrice = trip.getRoute().getBasePrice();
    BigDecimal totalPrice = basePrice.multiply(BigDecimal.valueOf(requestedSeats.size()));

    // 7. Create CoachBooking
    CoachBooking booking =
        CoachBooking.builder()
            .user(user)
            .coachTrip(trip)
            .basePrice(basePrice)
            .totalPrice(totalPrice)
            .contactName(request.getContactName())
            .contactPhone(request.getContactPhone())
            .status(BookingStatus.PENDING)
            .paymentDeadline(LocalDateTime.now().plusMinutes(15)) // 15 mins to pay
            .build();

    booking = coachBookingRepository.save(booking);

    // 8. Create CoachBookingSeats
    List<CoachBookingSeat> bookingSeatsToSave = new ArrayList<>();
    for (SeatLayoutItem seat : requestedSeats) {
      CoachBookingSeat bookingSeat = new CoachBookingSeat();
      bookingSeat.setCoachBooking(booking);
      bookingSeat.setSeatLayoutItem(seat);
      bookingSeatsToSave.add(bookingSeat);
    }
    coachBookingSeatRepository.saveAll(bookingSeatsToSave);

    // 9. Initiate VNPAY Payment
    var paymentRequest =
        InitiatePaymentRequest.builder()
            .bookingId(booking.getId())
            .amount(booking.getTotalPrice())
            .ipAddress(ipAddress)
            .build();

    PaymentInitiationResponse paymentResponse =
        paymentService.initiateCoachPayment(booking.getId(), paymentRequest, userId);

    // 10. Return response
    return CoachBookingResponse.builder()
        .id(booking.getId())
        .tripId(trip.getId())
        .departureTime(trip.getDepartureTime())
        .originDestination(trip.getRoute().getOriginDestination().getName())
        .destinationDestination(trip.getRoute().getDestinationDestination().getName())
        .basePrice(booking.getBasePrice())
        .totalPrice(booking.getTotalPrice())
        .paymentDeadline(booking.getPaymentDeadline())
        .contactName(booking.getContactName())
        .contactPhone(booking.getContactPhone())
        .status(booking.getStatus())
        .bookedSeatNames(
            requestedSeats.stream().map(SeatLayoutItem::getSeatName).collect(Collectors.toList()))
        .payment(paymentResponse)
        .build();
  }

  @Override
  @Transactional
  public PaymentInitiationResponse generatePaymentUrl(
      UUID bookingId, InitiatePaymentRequest request, UUID userId) {
    return paymentService.initiateCoachPayment(bookingId, request, userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CoachBookingSummaryResponse> getMyBookings(
      UUID userId, BookingStatus status, Pageable pageable) {

    Page<CoachBooking> bookingPage;
    if (status != null) {
      bookingPage = coachBookingRepository.findByUser_IdAndStatus(userId, status, pageable);
    } else {
      bookingPage = coachBookingRepository.findByUser_Id(userId, pageable);
    }

    List<UUID> bookingIds = bookingPage.getContent().stream().map(CoachBooking::getId).toList();

    java.util.Map<UUID, Integer> seatCountMap = new java.util.HashMap<>();
    if (!bookingIds.isEmpty()) {
      coachBookingSeatRepository
          .countSeatsByBookingIds(bookingIds)
          .forEach(row -> seatCountMap.put((UUID) row[0], ((Number) row[1]).intValue()));
    }

    return bookingPage.map(
        booking ->
            coachMapper.toCoachBookingSummaryResponse(
                booking, seatCountMap.getOrDefault(booking.getId(), 0)));
  }

  @Override
  @Transactional(readOnly = true)
  public CoachBookingDetailResponse getBookingDetail(UUID bookingId, UUID userId) {
    CoachBooking booking =
        coachBookingRepository
            .findByIdWithDetails(bookingId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(BookingErrorCode.BOOKING_ACCESS_DENIED);
    }

    List<String> bookedSeatNames =
        booking.getBookedSeats().stream()
            .map(bs -> bs.getSeatLayoutItem().getSeatName())
            .collect(Collectors.toList());

    PaymentTransaction payment =
        paymentTransactionRepository
            .findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
                booking.getId(), BookingType.COACH_BOOKING)
            .orElse(null);

    return coachMapper.toCoachBookingDetailResponse(booking, bookedSeatNames, payment);
  }

  @Override
  @Transactional
  public CancelBookingResponse cancelBooking(
      UUID bookingId, CancelBookingRequest request, UUID userId) {

    // 1. Load booking with details
    CoachBooking booking =
        coachBookingRepository
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

    CoachTrip trip = booking.getCoachTrip();
    if (trip.getDepartureTime().isBefore(LocalDateTime.now())) {
      throw new BaseAppException(BookingErrorCode.CANNOT_CANCEL_IN_PROGRESS);
    }

    // 4. Cancel booking & release seats
    booking.setStatus(BookingStatus.CANCELLED);
    coachBookingRepository.save(booking);

    if (trip.getStatus() == CoachTripStatus.FULL) {
      trip.setStatus(CoachTripStatus.OPEN);
      coachTripRepository.save(trip);
      log.info("CoachTrip {} transitioned FULL → OPEN", trip.getId());
    }

    // 6. Handle refund if booking was PAID
    if (currentStatus == BookingStatus.PAID) {
      return processRefund(booking, trip, request);
    }

    // PENDING booking
    return CancelBookingResponse.builder()
        .bookingId(booking.getId())
        .bookingStatus(BookingStatus.CANCELLED)
        .refundMessage("Booking cancelled. No payment was made.")
        .build();
  }

  private CancelBookingResponse processRefund(
      CoachBooking booking, CoachTrip trip, CancelBookingRequest request) {

    PaymentTransaction payment =
        paymentTransactionRepository
            .findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
                booking.getId(), BookingType.COACH_BOOKING)
            .orElse(null);

    if (payment == null) {
      return CancelBookingResponse.builder()
          .bookingId(booking.getId())
          .bookingStatus(BookingStatus.CANCELLED)
          .refundMessage("Booking cancelled. No payment transaction found.")
          .build();
    }

    // Calculate refund percentage from RefundPolicy (HOURS)
    long hoursBeforeDeparture =
        ChronoUnit.HOURS.between(LocalDateTime.now(), trip.getDepartureTime());
    BigDecimal refundPct =
        calculateRefundPercentage(trip.getRoute().getRefundPolicy(), hoursBeforeDeparture);

    BigDecimal refundAmount =
        booking
            .getTotalPrice()
            .multiply(refundPct)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    // Create RefundRequest
    RefundRequest refundRequest =
        RefundRequest.builder()
            .paymentTransaction(payment)
            .user(booking.getUser())
            .requestedAmount(refundAmount)
            .customerReason(request != null ? request.getReason() : null)
            .build();
    refundRequestRepository.save(refundRequest);

    log.info(
        "Refund request created for coach booking {}: {}% = {} VND",
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

  private BigDecimal calculateRefundPercentage(RefundPolicy policy, long hoursBeforeDeparture) {
    if (policy == null || policy.getRules() == null || policy.getRules().isEmpty()) {
      return BigDecimal.ZERO;
    }

    return policy.getRules().stream()
        .filter(rule -> rule.getTimeUnit() == RefundTimeUnit.HOURS)
        .filter(rule -> hoursBeforeDeparture >= rule.getTimeBefore())
        .max(java.util.Comparator.comparing(RefundPolicyRule::getTimeBefore))
        .map(RefundPolicyRule::getRefundPercentage)
        .orElse(BigDecimal.ZERO);
  }
}
