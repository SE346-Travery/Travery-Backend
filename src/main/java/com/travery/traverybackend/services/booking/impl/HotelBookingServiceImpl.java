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
import com.travery.traverybackend.dtos.response.booking.StayBillResponse;
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
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.AddOnOrderStatus;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.RefundStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.HotelBookingMapper;
import com.travery.traverybackend.mappers.TourBookingMapper;
import com.travery.traverybackend.repositories.booking.AddOnOrderRepository;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.HotelBookingDetailRepository;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.finance.PaymentTransactionRepository;
import com.travery.traverybackend.repositories.finance.RefundRequestRepository;
import com.travery.traverybackend.repositories.hotel.HotelServiceRepository;
import com.travery.traverybackend.repositories.hotel.RoomRepository;
import com.travery.traverybackend.repositories.hotel.RoomTypeRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.booking.HotelBookingService;
import com.travery.traverybackend.services.booking.PaymentService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
public class HotelBookingServiceImpl implements HotelBookingService {

  private final HotelBookingRepository hotelBookingRepository;
  private final HotelBookingDetailRepository hotelBookingDetailRepository;
  private final RoomTypeRepository roomTypeRepository;
  private final RoomRepository roomRepository;
  private final BookingMemberRepository bookingMemberRepository;
  private final UserRepository userRepository;
  private final TourInstanceRepository tourInstanceRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final AddOnOrderRepository addOnOrderRepository;
  private final HotelServiceRepository hotelServiceRepository;
  private final PaymentService paymentService;
  private final HotelBookingMapper hotelBookingMapper;
  private final TourBookingMapper tourBookingMapper;

  @Override
  @Transactional
  public HotelBookingResponse createBooking(CreateHotelBookingRequest request, UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

    TourInstance tourInstance = null;
    if (request.getTourInstanceId() != null) {
      tourInstance =
          tourInstanceRepository
              .findById(request.getTourInstanceId())
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.NOT_FOUND, "TourInstance not found"));
    }

    BigDecimal totalPrice = BigDecimal.ZERO;
    List<HotelBookingDetail> details = new ArrayList<>();

    for (HotelBookingRequestDetail detailReq : request.getRooms()) {
      RoomType roomType =
          roomTypeRepository
              .findById(detailReq.getRoomTypeId())
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.NOT_FOUND, "RoomType not found"));

      // 1. Availability check
      int totalRooms = roomRepository.countByRoomType_Id(roomType.getId());
      Integer bookedRooms =
          hotelBookingDetailRepository.sumBookedQuantity(
              roomType.getId(),
              detailReq.getStartDate(),
              detailReq.getEndDate(),
              List.of(BookingStatus.PENDING, BookingStatus.PAID));
      if (bookedRooms == null) bookedRooms = 0;

      if (totalRooms - bookedRooms < detailReq.getQuantity()) {
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST, "Not enough rooms available for " + roomType.getName());
      }

      // 2. Price calculation
      long nights = ChronoUnit.DAYS.between(detailReq.getStartDate(), detailReq.getEndDate());
      if (nights <= 0) {
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST, "Check-out date must be after check-in");
      }
      BigDecimal itemPrice =
          roomType.getBasePrice().multiply(BigDecimal.valueOf(nights * detailReq.getQuantity()));
      totalPrice = totalPrice.add(itemPrice);

      details.add(
          HotelBookingDetail.builder()
              .roomType(roomType)
              .quantity(detailReq.getQuantity())
              .priceAtBooking(roomType.getBasePrice())
              .startDate(detailReq.getStartDate())
              .endDate(detailReq.getEndDate())
              .build());
    }

    // 3. Create HotelBooking
    HotelBooking booking =
        HotelBooking.builder()
            .user(user)
            .tourInstance(tourInstance)
            .totalPrice(totalPrice)
            .paymentDeadline(LocalDateTime.now().plusMinutes(15))
            .status(BookingStatus.PENDING)
            .build();

    booking = hotelBookingRepository.save(booking);

    // 4. Save details and members
    for (HotelBookingDetail detail : details) {
      detail.setHotelBooking(booking);
    }
    hotelBookingDetailRepository.saveAll(details);

    List<BookingMember> members = new ArrayList<>();
    for (BookingMemberRequest memberReq : request.getMembers()) {
      BookingMember member = tourBookingMapper.toBookingMember(memberReq);
      member.setBookingId(booking.getId());
      member.setBookingType(BookingType.HOTEL_BOOKING);
      members.add(member);
    }
    bookingMemberRepository.saveAll(members);

    // 5. Initiate Payment
    InitiatePaymentRequest paymentReq = new InitiatePaymentRequest();
    paymentReq.setIpAddress(request.getIpAddress());
    PaymentInitiationResponse paymentResponse =
        paymentService.initiatePayment(
            booking.getId(), paymentReq, userId, BookingType.HOTEL_BOOKING);

    return hotelBookingMapper.toHotelBookingResponse(booking, members, paymentResponse);
  }

  @Override
  public Page<HotelBookingSummaryResponse> getMyBookings(
      UUID userId, BookingStatus status, Pageable pageable) {
    Page<HotelBooking> bookings;
    if (status != null) {
      bookings = hotelBookingRepository.findAllByUser_IdAndStatus(userId, status, pageable);
    } else {
      bookings = hotelBookingRepository.findAllByUser_Id(userId, pageable);
    }

    return bookings.map(
        b -> {
          int guestCount =
              bookingMemberRepository.countByBookingIdAndBookingType(
                  b.getId(), BookingType.HOTEL_BOOKING);
          HotelBookingSummaryResponse res =
              hotelBookingMapper.toHotelBookingSummaryResponse(b, guestCount);
          // Set Hotel Name - need to fetch from first detail
          List<HotelBookingDetail> details =
              hotelBookingDetailRepository.findAllByHotelBooking_Id(b.getId());
          if (!details.isEmpty()) {
            res.setHotelName(details.get(0).getRoomType().getHotel().getName());
          }
          return res;
        });
  }

  @Override
  public HotelBookingDetailResponse getBookingDetail(UUID bookingId, UUID userId) {
    HotelBooking booking =
        hotelBookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
    List<BookingMember> members =
        bookingMemberRepository.findAllByBookingIdAndBookingType(
            bookingId, BookingType.HOTEL_BOOKING);

    HotelBookingDetailResponse response =
        hotelBookingMapper.toHotelBookingDetailResponse(booking, details, members);

    if (!details.isEmpty()) {
      Hotel hotel = details.get(0).getRoomType().getHotel();
      response.setHotelName(hotel.getName());
      response.setHotelAddress(hotel.getAddress());
    }

    return response;
  }

  @Override
  public CancelBookingResponse getCancelQuote(UUID bookingId, UUID userId) {
    HotelBooking booking =
        hotelBookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    if (booking.getStatus() == BookingStatus.CANCELLED) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Booking is already cancelled");
    }

    if (booking.getStatus() == BookingStatus.PENDING) {
      return CancelBookingResponse.builder()
          .refundAmount(BigDecimal.ZERO)
          .refundPercentage(BigDecimal.ZERO)
          .refundMessage("Booking cancelled. No payment was made.")
          .build();
    }

    // PAID booking
    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
    if (details.isEmpty()) {
      throw new BaseAppException(WebErrorCode.INTERNAL_SERVER_ERROR, "Booking details missing");
    }

    Hotel hotel = details.get(0).getRoomType().getHotel();
    LocalDate checkInDate =
        details.stream().map(HotelBookingDetail::getStartDate).min(LocalDate::compareTo).get();

    long daysBeforeCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);

    BigDecimal refundPct = calculateRefundPercentage(hotel.getRefundPolicy(), daysBeforeCheckIn);
    BigDecimal refundAmount = booking.getTotalPrice().multiply(refundPct);

    return CancelBookingResponse.builder()
        .refundAmount(refundAmount)
        .refundPercentage(refundPct)
        .refundMessage(
            String.format(
                "Estimated refund: %s%% (%s VND).",
                refundPct.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString(),
                refundAmount.stripTrailingZeros().toPlainString()))
        .build();
  }

  @Override
  @Transactional
  public void cancelBooking(UUID bookingId, CancelBookingRequest request, UUID userId) {
    HotelBooking booking =
        hotelBookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    if (booking.getStatus() == BookingStatus.CANCELLED) {
      return;
    }

    if (booking.getStatus() == BookingStatus.PENDING) {
      booking.setStatus(BookingStatus.CANCELLED);
      hotelBookingRepository.save(booking);
      return;
    }

    // PAID booking
    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
    Hotel hotel = details.get(0).getRoomType().getHotel();
    LocalDate checkInDate =
        details.stream().map(HotelBookingDetail::getStartDate).min(LocalDate::compareTo).get();

    long daysBeforeCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);

    BigDecimal refundPct = calculateRefundPercentage(hotel.getRefundPolicy(), daysBeforeCheckIn);
    BigDecimal refundAmount = booking.getTotalPrice().multiply(refundPct);

    PaymentTransaction transaction =
        paymentTransactionRepository
            .findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
                booking.getId(), BookingType.HOTEL_BOOKING)
            .orElseThrow(
                () ->
                    new BaseAppException(
                        WebErrorCode.INTERNAL_SERVER_ERROR, "Payment transaction not found"));

    RefundRequest refundRequest =
        RefundRequest.builder()
            .paymentTransaction(transaction)
            .user(booking.getUser())
            .requestedAmount(refundAmount)
            .customerReason(request.getReason())
            .status(RefundStatus.PENDING)
            .build();

    refundRequestRepository.save(refundRequest);

    booking.setStatus(BookingStatus.CANCELLED);
    hotelBookingRepository.save(booking);
  }

  @Override
  @Transactional
  public AddOnOrderResponse createAddOnOrder(
      UUID bookingId, CreateAddOnOrderRequest request, UUID userId) {
    HotelBooking booking =
        hotelBookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    if (booking.getStatus() != BookingStatus.CHECKED_IN) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Service ordering only available when checked-in");
    }

    HotelService service =
        hotelServiceRepository
            .findById(request.getServiceId())
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Service not found"));

    // Verify service belongs to the same hotel
    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
    if (details.isEmpty()
        || !details.get(0).getRoomType().getHotel().getId().equals(service.getHotel().getId())) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Service not offered by this hotel");
    }

    BigDecimal totalPrice = service.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

    AddOnOrder order =
        AddOnOrder.builder()
            .hotelBooking(booking)
            .hotelService(service)
            .quantity(request.getQuantity())
            .totalPrice(totalPrice)
            .scheduledTime(request.getScheduledTime())
            .status(AddOnOrderStatus.PENDING)
            .build();

    order = addOnOrderRepository.save(order);

    return mapToAddOnOrderResponse(order);
  }

  @Override
  public StayBillResponse getStayBill(UUID bookingId, UUID userId) {
    HotelBooking booking =
        hotelBookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    if (!booking.getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    if (booking.getStatus() != BookingStatus.CHECKED_IN
        && booking.getStatus() != BookingStatus.CHECKED_OUT) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Bill only available after check-in");
    }

    List<AddOnOrder> orders = addOnOrderRepository.findAllByHotelBooking_Id(bookingId);
    List<AddOnOrderResponse> orderResponses =
        orders.stream().map(this::mapToAddOnOrderResponse).toList();

    BigDecimal totalAddOnCharges =
        orders.stream()
            .filter(o -> o.getStatus() != AddOnOrderStatus.CANCELLED)
            .map(AddOnOrder::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return StayBillResponse.builder()
        .hotelBookingId(bookingId)
        .roomCharges(booking.getTotalPrice())
        .addOnOrders(orderResponses)
        .totalAddOnCharges(totalAddOnCharges)
        .totalBill(booking.getTotalPrice().add(totalAddOnCharges))
        .build();
  }

  @Override
  @Transactional
  public void cancelAddOnOrder(UUID orderId, UUID userId) {
    AddOnOrder order =
        addOnOrderRepository
            .findById(orderId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Order not found"));

    if (!order.getHotelBooking().getUser().getId().equals(userId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    if (order.getStatus() == AddOnOrderStatus.CANCELLED) {
      return;
    }

    if (order.getStatus() == AddOnOrderStatus.DELIVERED) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Cannot cancel a delivered service");
    }

    if (LocalDateTime.now().plusHours(2).isAfter(order.getScheduledTime())) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Orders must be cancelled at least 2 hours in advance");
    }

    order.setStatus(AddOnOrderStatus.CANCELLED);
    addOnOrderRepository.save(order);
  }

  private AddOnOrderResponse mapToAddOnOrderResponse(AddOnOrder order) {
    return AddOnOrderResponse.builder()
        .id(order.getId())
        .serviceName(order.getHotelService().getName())
        .category(order.getHotelService().getCategory().name())
        .quantity(order.getQuantity())
        .unitPrice(order.getHotelService().getPrice())
        .totalPrice(order.getTotalPrice())
        .scheduledTime(order.getScheduledTime())
        .status(order.getStatus().name())
        .build();
  }

  private BigDecimal calculateRefundPercentage(RefundPolicy policy, long daysBefore) {
    if (policy == null || policy.getRules() == null) {
      return BigDecimal.ZERO;
    }
    return policy.getRules().stream()
        .filter(rule -> daysBefore >= rule.getDaysBefore())
        .max(Comparator.comparingInt(RefundPolicyRule::getDaysBefore))
        .map(RefundPolicyRule::getRefundPercentage)
        .orElse(BigDecimal.ZERO);
  }
}
