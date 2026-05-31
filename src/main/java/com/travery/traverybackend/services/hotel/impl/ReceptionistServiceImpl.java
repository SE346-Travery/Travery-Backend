package com.travery.traverybackend.services.hotel.impl;

import com.travery.traverybackend.dtos.request.staff.CheckInRequest;
import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.booking.BookingMemberResponse;
import com.travery.traverybackend.dtos.response.staff.*;
import com.travery.traverybackend.entities.booking.*;
import com.travery.traverybackend.entities.hotel.*;
import com.travery.traverybackend.entities.user.Receptionist;
import com.travery.traverybackend.enums.booking.AddOnOrderStatus;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.hotel.RoomStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.ReceptionistMapper;
import com.travery.traverybackend.mappers.TourBookingMapper;
import com.travery.traverybackend.repositories.booking.*;
import com.travery.traverybackend.repositories.hotel.*;
import com.travery.traverybackend.repositories.user.ReceptionistRepository;
import com.travery.traverybackend.services.hotel.ReceptionistService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceptionistServiceImpl implements ReceptionistService {

  private final ReceptionistRepository receptionistRepository;
  private final HotelBookingRepository hotelBookingRepository;
  private final HotelBookingDetailRepository hotelBookingDetailRepository;
  private final RoomRepository roomRepository;
  private final RoomAssignmentRepository roomAssignmentRepository;
  private final BookingMemberRepository bookingMemberRepository;
  private final AddOnOrderRepository addOnOrderRepository;
  private final TourBookingMapper tourBookingMapper;
  private final ReceptionistMapper receptionistMapper;

  @Override
  public ReceptionistDashboardResponse getDashboard(UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    UUID hotelId = receptionist.getHotel().getId();

    long available = roomRepository.countByHotel_IdAndStatus(hotelId, RoomStatus.AVAILABLE);
    long occupied = roomRepository.countByHotel_IdAndStatus(hotelId, RoomStatus.OCCUPIED);
    long cleaning = roomRepository.countByHotel_IdAndStatus(hotelId, RoomStatus.CLEANING);
    long maintenance = roomRepository.countByHotel_IdAndStatus(hotelId, RoomStatus.MAINTENANCE);

    LocalDate today = LocalDate.now();
    long checkInCount = hotelBookingDetailRepository.countTodayCheckIns(hotelId, today);
    long checkOutCount = hotelBookingDetailRepository.countTodayCheckOuts(hotelId, today);

    List<HotelBooking> checkInBookings =
        hotelBookingRepository.findTodayCheckInBookings(hotelId, today);
    List<HotelBooking> checkOutBookings =
        hotelBookingRepository.findTodayCheckOutBookings(hotelId, today);

    List<DashboardGuestResponse> checkInQueue =
        checkInBookings.stream().map(this::mapToDashboardGuestResponse).toList();
    List<DashboardGuestResponse> checkOutQueue =
        checkOutBookings.stream().map(this::mapToDashboardGuestResponse).toList();

    return ReceptionistDashboardResponse.builder()
        .availableRooms(available)
        .occupiedRooms(occupied)
        .cleaningRooms(cleaning)
        .maintenanceRooms(maintenance)
        .todayCheckInCount(checkInCount)
        .todayCheckOutCount(checkOutCount)
        .checkInQueue(checkInQueue)
        .checkOutQueue(checkOutQueue)
        .build();
  }

  private DashboardGuestResponse mapToDashboardGuestResponse(HotelBooking booking) {
    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(booking.getId());

    int memberCount =
        bookingMemberRepository.countByBookingIdAndBookingType(
            booking.getId(), BookingType.HOTEL_BOOKING);

    int totalRooms = details.stream().mapToInt(HotelBookingDetail::getQuantity).sum();

    Map<String, Integer> breakdown =
        details.stream()
            .collect(
                Collectors.toMap(
                    d -> d.getRoomType().getName(), HotelBookingDetail::getQuantity, Integer::sum));

    return DashboardGuestResponse.builder()
        .bookingId(booking.getId())
        .touristName(booking.getUser().getFullName())
        .phoneNumber(booking.getUser().getPhoneNumber())
        .memberCount(memberCount)
        .totalRooms(totalRooms)
        .roomTypeBreakdown(breakdown)
        .build();
  }

  @Override
  public Page<ReceptionistBookingSummaryResponse> getBookings(
      UUID receptionistId,
      LocalDate date,
      String guestName,
      BookingStatus status,
      Pageable pageable) {
    Receptionist receptionist = getReceptionist(receptionistId);
    UUID hotelId = receptionist.getHotel().getId();

    Page<HotelBooking> bookings =
        hotelBookingRepository.findReceptionistQueue(hotelId, date, guestName, status, pageable);

    return bookings.map(
        b -> {
          List<HotelBookingDetail> details =
              hotelBookingDetailRepository.findAllByHotelBooking_Id(b.getId());
          LocalDate checkIn =
              details.stream()
                  .map(HotelBookingDetail::getStartDate)
                  .min(LocalDate::compareTo)
                  .get();
          LocalDate checkOut =
              details.stream().map(HotelBookingDetail::getEndDate).max(LocalDate::compareTo).get();

          return ReceptionistBookingSummaryResponse.builder()
              .id(b.getId())
              .guestName(b.getUser().getFullName())
              .phoneNumber(b.getUser().getPhoneNumber())
              .checkInDate(checkIn)
              .checkOutDate(checkOut)
              .status(b.getStatus().name())
              .build();
        });
  }

  @Override
  public ReceptionistBookingDetailResponse getBookingDetail(UUID bookingId, UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    HotelBooking booking = getBooking(bookingId, receptionist.getHotel().getId());

    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
    List<BookingMember> members =
        bookingMemberRepository.findAllByBookingIdAndBookingType(
            bookingId, BookingType.HOTEL_BOOKING);
    List<AddOnOrder> addOnOrders = addOnOrderRepository.findAllByHotelBooking_Id(bookingId);

    LocalDate checkIn =
        details.stream().map(HotelBookingDetail::getStartDate).min(LocalDate::compareTo).get();
    LocalDate checkOut =
        details.stream().map(HotelBookingDetail::getEndDate).max(LocalDate::compareTo).get();

    List<RoomAllocationResponse> allocations =
        details.stream()
            .map(
                d -> {
                  List<RoomAssignment> assignments =
                      roomAssignmentRepository.findAllByHotelBookingDetail_Id(d.getId());
                  List<String> roomNumbers =
                      assignments.stream()
                          .map(a -> a.getRoom().getRoomNumber())
                          .collect(Collectors.toList());

                  return RoomAllocationResponse.builder()
                      .roomTypeName(d.getRoomType().getName())
                      .quantity(d.getQuantity())
                      .assignedRoomNumbers(roomNumbers)
                      .build();
                })
            .collect(Collectors.toList());

    List<BookingMemberResponse> manifest =
        members.stream().map(tourBookingMapper::toBookingMemberResponse).toList();

    List<AddOnOrderResponse> orders =
        addOnOrders.stream().map(receptionistMapper::toAddOnOrderResponse).toList();

    return ReceptionistBookingDetailResponse.builder()
        .id(booking.getId())
        .guestName(booking.getUser().getFullName())
        .phoneNumber(booking.getUser().getPhoneNumber())
        .checkInDate(checkIn)
        .checkOutDate(checkOut)
        .status(booking.getStatus().name())
        .totalPrice(booking.getTotalPrice())
        .manifest(manifest)
        .roomAllocations(allocations)
        .addOnOrders(orders)
        .build();
  }

  @Override
  public List<ReceptionistRoomResponse> getAvailableRooms(UUID roomTypeId, UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    List<Room> rooms =
        roomRepository.findAllByRoomType_IdAndStatus(roomTypeId, RoomStatus.AVAILABLE);

    // Verify hotel ownership
    return rooms.stream()
        .filter(r -> r.getHotel().getId().equals(receptionist.getHotel().getId()))
        .map(receptionistMapper::toReceptionistRoomResponse)
        .toList();
  }

  @Override
  @Transactional
  public void checkIn(UUID bookingId, CheckInRequest request, UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    HotelBooking booking = getBooking(bookingId, receptionist.getHotel().getId());

    if (booking.getStatus() != BookingStatus.PAID) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Booking must be PAID to check-in");
    }

    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
    List<UUID> requestedRoomIds = new ArrayList<>(request.getRoomIds());

    for (HotelBookingDetail detail : details) {
      int needed = detail.getQuantity();
      List<RoomAssignment> assignments = new ArrayList<>();

      for (int i = 0; i < needed; i++) {
        if (requestedRoomIds.isEmpty()) {
          throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Insufficient rooms provided");
        }
        UUID roomId = requestedRoomIds.remove(0);
        Room room =
            roomRepository
                .findById(roomId)
                .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room not found"));

        if (room.getStatus() != RoomStatus.AVAILABLE
            || !room.getRoomType().getId().equals(detail.getRoomType().getId())) {
          throw new BaseAppException(
              WebErrorCode.BAD_REQUEST, "Room " + room.getRoomNumber() + " is not available");
        }

        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        assignments.add(RoomAssignment.builder().hotelBookingDetail(detail).room(room).build());
      }
      roomAssignmentRepository.saveAll(assignments);
    }

    booking.setStatus(BookingStatus.CHECKED_IN);
    booking.setActualCheckInTime(LocalDateTime.now());
    hotelBookingRepository.save(booking);
  }

  @Override
  @Transactional
  public CheckOutResponse checkOut(UUID bookingId, UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    HotelBooking booking = getBooking(bookingId, receptionist.getHotel().getId());

    if (booking.getStatus() != BookingStatus.CHECKED_IN) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Booking must be CHECKED_IN to check-out");
    }

    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
    List<AddOnOrder> orders = addOnOrderRepository.findAllByHotelBooking_Id(bookingId);

    // Calculate Bill
    BigDecimal addOnCharges =
        orders.stream()
            .filter(o -> o.getStatus() == AddOnOrderStatus.DELIVERED)
            .map(AddOnOrder::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Late fees logic (simplified: check if now is after 12:00 PM of endDate)
    LocalDate endDate =
        details.stream().map(HotelBookingDetail::getEndDate).max(LocalDate::compareTo).get();
    LocalDateTime checkOutDeadline = LocalDateTime.of(endDate, LocalTime.of(12, 0));
    BigDecimal lateFees = BigDecimal.ZERO;
    if (LocalDateTime.now().isAfter(checkOutDeadline)) {
      // 10% of total price as late fee for simplicity
      lateFees = booking.getTotalPrice().multiply(BigDecimal.valueOf(0.1));
    }

    List<AddOnOrderResponse> unpaidAddOns =
        orders.stream()
            .filter(o -> o.getStatus() == AddOnOrderStatus.PENDING)
            .map(receptionistMapper::toAddOnOrderResponse)
            .toList();

    CheckOutResponse response =
        CheckOutResponse.builder()
            .bookingId(bookingId)
            .roomCharges(booking.getTotalPrice())
            .addOnCharges(addOnCharges)
            .lateFees(lateFees)
            .totalBill(booking.getTotalPrice().add(addOnCharges).add(lateFees))
            .unpaidAddOns(unpaidAddOns)
            .build();

    // Release rooms
    for (HotelBookingDetail detail : details) {
      List<RoomAssignment> assignments =
          roomAssignmentRepository.findAllByHotelBookingDetail_Id(detail.getId());
      for (RoomAssignment assignment : assignments) {
        Room room = assignment.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
      }
    }

    booking.setStatus(BookingStatus.CHECKED_OUT);
    booking.setActualCheckOutTime(LocalDateTime.now());
    hotelBookingRepository.save(booking);

    return response;
  }

  @Override
  public List<ReceptionistRoomResponse> getAllRooms(UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    return roomRepository.findAllByHotel_Id(receptionist.getHotel().getId()).stream()
        .map(receptionistMapper::toReceptionistRoomResponse)
        .toList();
  }

  @Override
  @Transactional
  public void updateRoomStatus(UUID roomId, RoomStatus status, UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room not found"));

    if (!room.getHotel().getId().equals(receptionist.getHotel().getId())) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    room.setStatus(status);
    roomRepository.save(room);
  }

  @Override
  public List<AddOnOrderResponse> getActiveAddOnOrders(UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    // Find all orders for the hotel that are PENDING
    return addOnOrderRepository.findActiveByHotelId(receptionist.getHotel().getId()).stream()
        .map(receptionistMapper::toAddOnOrderResponse)
        .toList();
  }

  @Override
  @Transactional
  public void updateAddOnOrderStatus(UUID orderId, AddOnOrderStatus status, UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    AddOnOrder order =
        addOnOrderRepository
            .findById(orderId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Order not found"));

    // Verify hotel
    if (!order.getHotelService().getHotel().getId().equals(receptionist.getHotel().getId())) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    order.setStatus(status);
    addOnOrderRepository.save(order);
  }

  private Receptionist getReceptionist(UUID id) {
    return receptionistRepository
        .findById(id)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Receptionist not found"));
  }

  private HotelBooking getBooking(UUID bookingId, UUID hotelId) {
    HotelBooking booking =
        hotelBookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Booking not found"));

    // Verify this booking belongs to the receptionist's hotel
    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
    if (details.isEmpty() || !details.get(0).getRoomType().getHotel().getId().equals(hotelId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Booking does not belong to this hotel");
    }
    return booking;
  }
}
