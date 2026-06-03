package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.dtos.request.booking.BookingMemberRequest;
import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateAddOnOrderRequest;
import com.travery.traverybackend.dtos.request.booking.CreateHotelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.HotelBookingRequestDetail;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.dtos.response.booking.AddOnBillResponse;
import com.travery.traverybackend.entities.booking.AddOnOrder;
import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.HotelBookingDetail;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.finance.RefundPolicyRule;
import com.travery.traverybackend.entities.finance.RefundRequest;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.hotel.HotelService;
import com.travery.traverybackend.entities.hotel.RoomType;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.AddOnOrderStatus;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.RefundStatus;
import com.travery.traverybackend.enums.finance.RefundTimeUnit;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.HotelBookingMapper;
import com.travery.traverybackend.repositories.booking.AddOnOrderRepository;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.HotelBookingDetailRepository;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.finance.PaymentTransactionRepository;
import com.travery.traverybackend.repositories.finance.RefundRequestRepository;
import com.travery.traverybackend.repositories.hotel.HotelServiceRepository;
import com.travery.traverybackend.repositories.hotel.RoomRepository;
import com.travery.traverybackend.repositories.hotel.RoomTypeRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.booking.HotelBookingService;
import com.travery.traverybackend.services.booking.PaymentService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
@Transactional(readOnly = true)
public class HotelBookingServiceImpl implements HotelBookingService {

  private final HotelBookingRepository hotelBookingRepository;
  private final HotelBookingDetailRepository hotelBookingDetailRepository;
  private final RoomRepository roomRepository;
  private final RoomTypeRepository roomTypeRepository;
  private final BookingMemberRepository bookingMemberRepository;
  private final UserRepository userRepository;
  private final HotelBookingMapper hotelBookingMapper;
  private final PaymentService paymentService;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final AddOnOrderRepository addOnOrderRepository;
  private final HotelServiceRepository hotelServiceRepository;

  @Override
  @Transactional
  public HotelBookingResponse createBooking(CreateHotelBookingRequest request, UUID userId) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

    HotelBooking booking = HotelBooking.builder()
        .user(user)
        .status(BookingStatus.PENDING)
        .paymentDeadline(LocalDateTime.now().plusMinutes(15))
        .contactName(request.getContactName())
        .contactPhone(request.getContactPhone())
        .specialRequests(request.getSpecialRequests())
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .build();

    BigDecimal total = BigDecimal.ZERO;
    List<HotelBookingDetail> details = new ArrayList<>();

    UUID currentHotelId = null;
    int totalAdultCapacity = 0;
    int totalChildCapacity = 0;

    for (HotelBookingRequestDetail detailRequest : request.getRooms()) {
      RoomType roomType = roomTypeRepository
          .findById(detailRequest.getRoomTypeId())
          .orElseThrow(
              () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));

      if (roomType.isDeleted()) {
        throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Room type is no longer available");
      }

      if (currentHotelId == null) {
        currentHotelId = roomType.getHotel().getId();
      } else if (!currentHotelId.equals(roomType.getHotel().getId())) {
        throw new BaseAppException(WebErrorCode.BAD_REQUEST,
            "All rooms in a single booking must belong to the same hotel");
      }

      // Check room availability
      int totalRooms = roomRepository.countByRoomType_IdAndIsDeletedFalse(roomType.getId());
      Integer bookedQuantity = hotelBookingDetailRepository.sumBookedQuantity(
          roomType.getId(),
          request.getStartDate(),
          request.getEndDate(),
          List.of(BookingStatus.PENDING, BookingStatus.PAID, BookingStatus.CHECKED_IN));

      int availableRooms = totalRooms - (bookedQuantity != null ? bookedQuantity : 0);
      if (detailRequest.getQuantity() > availableRooms) {
        throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Not enough rooms available for the requested dates");
      }

      long nights = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
      BigDecimal price = roomType
          .getBasePrice()
          .multiply(BigDecimal.valueOf(nights))
          .multiply(BigDecimal.valueOf(detailRequest.getQuantity()));

      total = total.add(price);
      totalAdultCapacity += roomType.getCapacityAdults() * detailRequest.getQuantity();
      totalChildCapacity += roomType.getCapacityChildren() * detailRequest.getQuantity();

      details.add(
          HotelBookingDetail.builder()
              .hotelBooking(booking)
              .roomType(roomType)
              .quantity(detailRequest.getQuantity())
              .priceAtBooking(price)
              .build());
    }

    // Check members capacity
    long adultCount = request.getMembers().stream()
        .filter(m -> ChronoUnit.YEARS.between(m.getDateOfBirth(), LocalDate.now()) >= 12)
        .count();
    long childCount = request.getMembers().size() - adultCount;

    if (adultCount > totalAdultCapacity || (adultCount + childCount) > (totalAdultCapacity + totalChildCapacity)) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Number of guests exceeds room capacity");
    }

    booking.setTotalPrice(total);
    booking = hotelBookingRepository.save(booking);
    hotelBookingDetailRepository.saveAll(details);

    // Save members
    List<BookingMember> members = new ArrayList<>();
    for (BookingMemberRequest memberRequest : request.getMembers()) {
      members.add(
          BookingMember.builder()
              .bookingId(booking.getId())
              .bookingType(BookingType.HOTEL_BOOKING)
              .fullName(memberRequest.getFullName())
              .identityNumber(memberRequest.getIdentityNumber())
              .dateOfBirth(memberRequest.getDateOfBirth())
              .memberType(memberRequest.getMemberType())
              .build());
    }
    bookingMemberRepository.saveAll(members);

    // Initiate payment
    InitiatePaymentRequest paymentReq = InitiatePaymentRequest.builder().ipAddress(request.getIpAddress()).build();

    PaymentInitiationResponse payment = paymentService.initiatePayment(
        booking.getId(), paymentReq, userId, BookingType.HOTEL_BOOKING);

    return hotelBookingMapper.toHotelBookingResponse(booking, members, payment);
  }

  @Override
  public Page<HotelBookingSummaryResponse> getMyBookings(
      UUID userId, BookingStatus status, Pageable pageable) {
    Page<HotelBooking> bookings = hotelBookingRepository.findAllByUser_Id(userId, pageable);

    if (bookings.isEmpty()) {
      return Page.empty();
    }

    List<UUID> bookingIds = bookings.getContent().stream().map(HotelBooking::getId).toList();

    Map<UUID, Integer> guestCountsMap = bookingMemberRepository
        .countByBookingIds(bookingIds, BookingType.HOTEL_BOOKING).stream()
        .collect(Collectors.toMap(
            row -> (UUID) row[0],
            row -> ((Number) row[1]).intValue(),
            (v1, v2) -> v1));

    Map<UUID, String> hotelNamesMap = hotelBookingDetailRepository
        .findHotelNamesByBookingIds(bookingIds).stream()
        .collect(Collectors.toMap(
            row -> (UUID) row[0],
            row -> (String) row[1],
            (v1, v2) -> v1));

    return bookings.map(
        b -> {
          int guests = guestCountsMap.getOrDefault(b.getId(), 0);
          HotelBookingSummaryResponse res = hotelBookingMapper.toHotelBookingSummaryResponse(b, guests);
          res.setHotelName(hotelNamesMap.get(b.getId()));
          return res;
        });
  }

  @Override
  public HotelBookingDetailResponse getBookingDetail(UUID bookingId, UUID userId) {
    HotelBooking booking = hotelBookingRepository
        .findById(bookingId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    List<HotelBookingDetail> details = hotelBookingDetailRepository
        .findAllWithRoomTypeAndHotelByHotelBooking_Id(bookingId);
    List<BookingMember> members = bookingMemberRepository.findAllByBookingIdAndBookingType(
        bookingId, BookingType.HOTEL_BOOKING);

    PaymentTransaction payment = paymentTransactionRepository
        .findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(bookingId, BookingType.HOTEL_BOOKING)
        .orElse(null);

    HotelBookingDetailResponse res = hotelBookingMapper.toHotelBookingDetailResponse(booking, details, members,
        payment);

    if (!details.isEmpty()) {
      Hotel hotel = details.get(0).getRoomType().getHotel();
      res.setHotelName(hotel.getName());
      res.setHotelAddress(hotel.getAddress());
    }

    return res;
  }

  @Override
  @Transactional
  public CancelBookingResponse cancelBooking(UUID bookingId, CancelBookingRequest request, UUID userId) {
    HotelBooking booking = hotelBookingRepository
        .findById(bookingId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    BookingStatus currentStatus = booking.getStatus();
    if (currentStatus == BookingStatus.CANCELLED) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Booking is already cancelled");
    }

    if (currentStatus == BookingStatus.CHECKED_IN
        || currentStatus == BookingStatus.CHECKED_OUT) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Cannot cancel a stayed booking");
    }

    booking.setStatus(BookingStatus.CANCELLED);
    hotelBookingRepository.save(booking);

    if (currentStatus == BookingStatus.PAID) {
      return processRefund(booking, request);
    }

    return CancelBookingResponse.builder()
        .bookingId(booking.getId())
        .bookingStatus(BookingStatus.CANCELLED)
        .refundMessage("Booking cancelled. No payment was made.")
        .build();
  }

  @Override
  public AddOnBillResponse getAddOnBill(UUID bookingId, UUID userId) {
    HotelBooking booking = hotelBookingRepository
        .findById(bookingId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    List<AddOnOrder> deliveredOrders = addOnOrderRepository.findAllByHotelBooking_Id(bookingId)
        .stream()
        .filter(o -> o.getStatus() == AddOnOrderStatus.DELIVERED)
        .toList();

    List<AddOnOrderResponse> orderResponses = deliveredOrders.stream()
        .map(hotelBookingMapper::toAddOnOrderResponse)
        .toList();

    BigDecimal addOnTotal = deliveredOrders.stream()
        .map(AddOnOrder::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return AddOnBillResponse.builder()
        .hotelBookingId(bookingId)
        .totalAddOnCharges(addOnTotal)
        .addOnOrders(orderResponses)
        .build();
  }

  @Override
  @Transactional
  public AddOnOrderResponse createAddOnOrder(
      UUID bookingId, CreateAddOnOrderRequest request, UUID userId) {
    HotelBooking booking = hotelBookingRepository
        .findById(bookingId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    if (booking.getStatus() != BookingStatus.CHECKED_IN) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Add-ons can only be ordered while checked-in");
    }

    if (request.getScheduledTime().toLocalDate().isAfter(booking.getEndDate())) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Scheduled time must be before or on check-out date");
    }

    HotelService service = hotelServiceRepository
        .findById(request.getServiceId())
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Service not found"));

    if (!service.isActive() || service.isDeleted()) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Service is currently unavailable");
    }

    List<HotelBookingDetail> details = hotelBookingDetailRepository
        .findAllWithRoomTypeAndHotelByHotelBooking_Id(bookingId);

    if (details.isEmpty()) {
      throw new BaseAppException(WebErrorCode.NOT_FOUND, "Booking details not found");
    }

    UUID hotelId = details.get(0).getRoomType().getHotel().getId();

    if (!service.getHotel().getId().equals(hotelId)) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "This service is not provided by your booked hotel");
    }

    AddOnOrder order = AddOnOrder.builder()
        .hotelBooking(booking)
        .hotelService(service)
        .quantity(request.getQuantity())
        .totalPrice(service.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
        .scheduledTime(request.getScheduledTime())
        .status(AddOnOrderStatus.PENDING)
        .build();

    order = addOnOrderRepository.save(order);
    return hotelBookingMapper.toAddOnOrderResponse(order);
  }

  @Override
  @Transactional
  public void cancelAddOnOrder(UUID orderId, UUID userId) {
    AddOnOrder order = addOnOrderRepository
        .findWithBookingById(orderId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Order not found"));

    if (!order.getHotelBooking().getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    if (order.getStatus() != AddOnOrderStatus.PENDING) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Only pending orders can be cancelled");
    }

    // Allow cancellation if created within the last 15 minutes (grace period) OR at
    // least 2 hours in advance
    boolean isWithinGracePeriod = order.getCreatedAt() != null
        && order.getCreatedAt().plusMinutes(15).isAfter(LocalDateTime.now());
    boolean isAdvancedEnough = LocalDateTime.now().plusHours(2).isBefore(order.getScheduledTime());

    if (!isWithinGracePeriod && !isAdvancedEnough) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST,
          "Orders must be cancelled within 15 minutes of booking or at least 2 hours in advance");
    }

    order.setStatus(AddOnOrderStatus.CANCELLED);
  }

  /**
   * Processes the refund logic for a cancelled hotel booking.
   * <p>
   * Workflow:
   * 1. Verifies if a payment transaction exists for the booking.
   * 2. Calculates the exact number of hours remaining until the hotel's check-in
   * time.
   * 3. Determines the refund percentage based on the hotel's refund policy.
   * 4. Calculates the exact refund amount.
   * 5. Creates and saves a new {@code RefundRequest} with PENDING status for
   * coordinator approval.
   * </p>
   *
   * @param booking The hotel booking being cancelled.
   * @param request The cancellation request containing the user's reason for
   *                cancellation.
   * @return CancelBookingResponse A response object containing the refund details
   *         and status.
   */
  private CancelBookingResponse processRefund(HotelBooking booking, CancelBookingRequest request) {
    PaymentTransaction payment = paymentTransactionRepository
        .findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
            booking.getId(), BookingType.HOTEL_BOOKING)
        .orElse(null);

    if (payment == null) {
      return CancelBookingResponse.builder()
          .bookingId(booking.getId())
          .bookingStatus(BookingStatus.CANCELLED)
          .refundMessage("Booking cancelled. No payment transaction found.")
          .build();
    }

    List<HotelBookingDetail> details = hotelBookingDetailRepository
        .findAllWithRoomTypeAndHotelByHotelBooking_Id(booking.getId());
    LocalDate checkIn = booking.getStartDate();

    Hotel hotel = details.get(0).getRoomType().getHotel();
    LocalTime checkInTime = hotel.getCheckInTime();
    if (checkInTime == null)
      checkInTime = LocalTime.of(12, 0);

    LocalDateTime exactCheckIn = LocalDateTime.of(checkIn, checkInTime);
    long hoursBefore = ChronoUnit.HOURS.between(LocalDateTime.now(), exactCheckIn);

    RefundPolicy policy = hotel.getRefundPolicy();

    BigDecimal refundPercent = calculateRefundPercentage(policy, hoursBefore);
    BigDecimal refundAmount = booking.getTotalPrice()
        .multiply(refundPercent)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    RefundRequest refundRequest = RefundRequest.builder()
        .user(booking.getUser())
        .paymentTransaction(payment)
        .requestedAmount(refundAmount)
        .customerReason(request != null ? request.getReason() : null)
        .bankName(request != null ? request.getBankName() : null)
        .accountNumber(request != null ? request.getAccountNumber() : null)
        .accountHolderName(request != null ? request.getAccountHolderName() : null)
        .status(RefundStatus.PENDING)
        .build();
    refundRequestRepository.save(refundRequest);

    return CancelBookingResponse.builder()
        .bookingId(booking.getId())
        .bookingStatus(BookingStatus.CANCELLED)
        .refundAmount(refundAmount)
        .refundPercentage(refundPercent)
        .refundStatus(refundRequest.getStatus())
        .refundMessage(String.format("Refund request submitted: %s%% (%s VND). Awaiting coordinator approval.",
            refundPercent.stripTrailingZeros().toPlainString(),
            refundAmount.stripTrailingZeros().toPlainString()))
        .build();
  }

  /**
   * Calculates the refund percentage based on the refund policy and the time
   * remaining before check-in.
   * <p>
   * Logic:
   * 1. Filters all rules that the user qualifies for (actual hours before
   * cancellation >= required hours of the rule).
   * 2. Finds the rule with the strictest time requirement among the qualified
   * ones (e.g., if both 3-day and 7-day rules are met, the 7-day rule is chosen).
   * 3. Returns the refund percentage of that rule. Returns 0 if no rule is
   * satisfied.
   * </p>
   *
   * @param policy      The refund policy containing rules (time limits and
   *                    percentages).
   * @param hoursBefore The number of hours remaining from the cancellation
   *                    request until check-in.
   * @return BigDecimal The refund percentage (e.g., 100, 50, 0).
   */
  private BigDecimal calculateRefundPercentage(RefundPolicy policy, long hoursBefore) {
    if (policy == null || policy.getRules() == null) {
      return BigDecimal.ZERO;
    }
    return policy.getRules().stream()
        .filter(rule -> {
          long ruleHours = rule.getTimeUnit() == RefundTimeUnit.DAYS
              ? rule.getTimeBefore() * 24L
              : rule.getTimeBefore();
          return hoursBefore >= ruleHours;
        })
        .max(Comparator
            .comparingLong(rule -> rule.getTimeUnit() == RefundTimeUnit.DAYS
                ? rule.getTimeBefore() * 24L
                : rule.getTimeBefore()))
        .map(RefundPolicyRule::getRefundPercentage)
        .orElse(BigDecimal.ZERO);
  }
}
