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
@Transactional(readOnly = true)
public class HotelBookingServiceImpl implements HotelBookingService {

  private final HotelBookingRepository hotelBookingRepository;
  private final HotelBookingDetailRepository hotelBookingDetailRepository;
  private final RoomTypeRepository roomTypeRepository;
  private final BookingMemberRepository bookingMemberRepository;
  private final UserRepository userRepository;
  private final HotelBookingMapper hotelBookingMapper;
  private final TourBookingMapper tourBookingMapper;
  private final PaymentService paymentService;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final AddOnOrderRepository addOnOrderRepository;
  private final HotelServiceRepository hotelServiceRepository;
  private final RoomRepository roomRepository;
  private final TourInstanceRepository tourInstanceRepository;

  @Override
  @Transactional
  public HotelBookingResponse createBooking(CreateHotelBookingRequest request, UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

    HotelBooking booking =
        HotelBooking.builder()
            .user(user)
            .status(BookingStatus.PENDING)
            .paymentDeadline(LocalDateTime.now().plusMinutes(15))
            .build();

    BigDecimal total = BigDecimal.ZERO;
    List<HotelBookingDetail> details = new ArrayList<>();

    for (HotelBookingRequestDetail detailRequest : request.getRooms()) {
      RoomType roomType =
          roomTypeRepository
              .findById(detailRequest.getRoomTypeId())
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));

      long nights =
          ChronoUnit.DAYS.between(detailRequest.getStartDate(), detailRequest.getEndDate());
      BigDecimal price =
          roomType
              .getBasePrice()
              .multiply(BigDecimal.valueOf(nights))
              .multiply(BigDecimal.valueOf(detailRequest.getQuantity()));

      total = total.add(price);

      details.add(
          HotelBookingDetail.builder()
              .hotelBooking(booking)
              .roomType(roomType)
              .startDate(detailRequest.getStartDate())
              .endDate(detailRequest.getEndDate())
              .quantity(detailRequest.getQuantity())
              .priceAtBooking(price)
              .build());
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
    InitiatePaymentRequest paymentReq =
        InitiatePaymentRequest.builder().ipAddress(request.getIpAddress()).build();

    PaymentInitiationResponse payment =
        paymentService.initiatePayment(
            booking.getId(), paymentReq, userId, BookingType.HOTEL_BOOKING);

    return hotelBookingMapper.toHotelBookingResponse(booking, members, payment);
  }

  @Override
  public Page<HotelBookingSummaryResponse> getMyBookings(
      UUID userId, BookingStatus status, Pageable pageable) {
    Page<HotelBooking> bookings = hotelBookingRepository.findAllByUser_Id(userId, pageable);
    return bookings.map(
        b -> {
          List<HotelBookingDetail> details =
              hotelBookingDetailRepository.findAllByHotelBooking_Id(b.getId());
          int guests =
              bookingMemberRepository.countByBookingIdAndBookingType(
                  b.getId(), BookingType.HOTEL_BOOKING);
          HotelBookingSummaryResponse res =
              hotelBookingMapper.toHotelBookingSummaryResponse(b, guests);
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

    HotelBookingDetailResponse res =
        hotelBookingMapper.toHotelBookingDetailResponse(booking, details, members);

    if (!details.isEmpty()) {
      Hotel hotel = details.get(0).getRoomType().getHotel();
      res.setHotelName(hotel.getName());
      res.setHotelAddress(hotel.getAddress());
    }

    // Add payment info if pending
    if (booking.getStatus() == BookingStatus.PENDING) {
      try {
        PaymentInitiationResponse payment =
            paymentService.initiatePayment(
                bookingId,
                InitiatePaymentRequest.builder().ipAddress("127.0.0.1").build(),
                userId,
                BookingType.HOTEL_BOOKING);
        res.setPayment(payment);
      } catch (Exception e) {
        // Ignore payment initiation errors in detail view
      }
    }

    return res;
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

    BigDecimal refundAmount = BigDecimal.ZERO;
    BigDecimal refundPercent = BigDecimal.ZERO;

    if (booking.getStatus() == BookingStatus.PAID) {
      List<HotelBookingDetail> details =
          hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
      LocalDate checkIn =
          details.stream().map(HotelBookingDetail::getStartDate).min(LocalDate::compareTo).get();

      long daysBefore = ChronoUnit.DAYS.between(LocalDate.now(), checkIn);
      Hotel hotel = details.get(0).getRoomType().getHotel();
      RefundPolicy policy = hotel.getRefundPolicy();

      refundPercent = calculateRefundPercentage(policy, daysBefore);
      refundAmount = booking.getTotalPrice().multiply(refundPercent);
    }

    return CancelBookingResponse.builder()
        .bookingId(bookingId)
        .bookingStatus(booking.getStatus())
        .refundAmount(refundAmount)
        .refundPercentage(refundPercent.multiply(BigDecimal.valueOf(100)))
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
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Booking is already cancelled");
    }

    if (booking.getStatus() == BookingStatus.CHECKED_IN
        || booking.getStatus() == BookingStatus.CHECKED_OUT) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Cannot cancel a stayed booking");
    }

    if (booking.getStatus() == BookingStatus.PAID) {
      // Calculate refund based on hotel policy
      List<HotelBookingDetail> details =
          hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
      LocalDate checkIn =
          details.stream().map(HotelBookingDetail::getStartDate).min(LocalDate::compareTo).get();

      long daysBefore = ChronoUnit.DAYS.between(LocalDate.now(), checkIn);
      Hotel hotel = details.get(0).getRoomType().getHotel();
      RefundPolicy policy = hotel.getRefundPolicy();

      BigDecimal refundPercent = calculateRefundPercentage(policy, daysBefore);
      BigDecimal refundAmount = booking.getTotalPrice().multiply(refundPercent);

      // Create refund request
      PaymentTransaction txn =
          paymentTransactionRepository
              .findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
                  bookingId, BookingType.HOTEL_BOOKING)
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Transaction not found"));

      RefundRequest refundRequest =
          RefundRequest.builder()
              .user(booking.getUser())
              .paymentTransaction(txn)
              .requestedAmount(refundAmount)
              .customerReason(request.getReason())
              .status(RefundStatus.PENDING)
              .build();
      refundRequestRepository.save(refundRequest);

      booking.setStatus(BookingStatus.CANCELLED);
    } else {
      booking.setStatus(BookingStatus.CANCELLED);
    }

    hotelBookingRepository.save(booking);
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

    List<AddOnOrder> orders = addOnOrderRepository.findAllByHotelBooking_Id(bookingId);
    List<AddOnOrderResponse> orderResponses =
        orders.stream().map(this::mapToAddOnOrderResponse).toList();

    BigDecimal addOnTotal =
        orders.stream()
            .filter(o -> o.getStatus() == AddOnOrderStatus.DELIVERED)
            .map(AddOnOrder::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return StayBillResponse.builder()
        .hotelBookingId(bookingId)
        .roomCharges(booking.getTotalPrice())
        .totalAddOnCharges(addOnTotal)
        .totalBill(booking.getTotalPrice().add(addOnTotal))
        .addOnOrders(orderResponses)
        .build();
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
          WebErrorCode.BAD_REQUEST, "Add-ons can only be ordered while checked-in");
    }

    HotelService service =
        hotelServiceRepository
            .findById(request.getServiceId())
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Service not found"));

    AddOnOrder order =
        AddOnOrder.builder()
            .hotelBooking(booking)
            .hotelService(service)
            .quantity(request.getQuantity())
            .totalPrice(service.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
            .scheduledTime(request.getScheduledTime())
            .status(AddOnOrderStatus.PENDING)
            .build();

    order = addOnOrderRepository.save(order);
    return mapToAddOnOrderResponse(order);
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

    if (order.getStatus() != AddOnOrderStatus.PENDING) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Only pending orders can be cancelled");
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
        .filter(rule -> daysBefore >= rule.getTimeBefore())
        .max(Comparator.comparingInt(RefundPolicyRule::getTimeBefore))
        .map(RefundPolicyRule::getRefundPercentage)
        .orElse(BigDecimal.ZERO);
  }
}
