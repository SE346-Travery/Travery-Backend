package com.travery.traverybackend.services.hotel.impl;

import com.travery.traverybackend.dtos.request.staff.CheckInRequest;
import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.staff.*;
import com.travery.traverybackend.entities.booking.*;
import com.travery.traverybackend.entities.hotel.*;
import com.travery.traverybackend.entities.user.Receptionist;
import com.travery.traverybackend.enums.booking.AddOnOrderStatus;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.enums.hotel.RoomStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.ReceptionistMapper;
import com.travery.traverybackend.repositories.booking.*;
import com.travery.traverybackend.repositories.hotel.*;
import com.travery.traverybackend.repositories.user.ReceptionistRepository;
import com.travery.traverybackend.services.common.NotificationService;
import com.travery.traverybackend.services.hotel.ReceptionistService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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
  private final NotificationService notificationService;
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
        hotelBookingRepository.findReceptionistQueue(
            hotelId, date, guestName, status != null ? status.name() : null, pageable);

    return bookings.map(
        b -> {
          LocalDate checkIn = b.getStartDate();
          LocalDate checkOut = b.getEndDate();

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

    // Fetch all assignments for this booking to prevent N+1
    List<RoomAssignment> allAssignments =
        roomAssignmentRepository.findAllByHotelBookingDetail_HotelBooking_Id(bookingId);

    // Group by HotelBookingDetail.id
    Map<UUID, List<RoomAssignment>> assignmentMap =
        allAssignments.stream()
            .collect(Collectors.groupingBy(a -> a.getHotelBookingDetail().getId()));

    List<RoomAllocationResponse> allocations =
        details.stream()
            .map(
                d -> {
                  List<RoomAssignment> assignments =
                      assignmentMap.getOrDefault(d.getId(), List.of());
                  List<String> roomNumbers =
                      assignments.stream().map(a -> a.getRoom().getRoomNumber()).toList();

                  return receptionistMapper.toRoomAllocationResponse(d, roomNumbers);
                })
            .toList();

    List<HotelGuestResponse> manifest =
        members.stream().map(receptionistMapper::toHotelGuestResponse).toList();

    List<AddOnOrderResponse> addOnOrdersResponse =
        addOnOrders.stream().map(receptionistMapper::toAddOnOrderResponse).toList();

    BigDecimal totalAddOnCharges =
        addOnOrders.stream()
            .map(AddOnOrder::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return receptionistMapper.toBookingDetailResponse(
        booking, totalAddOnCharges, manifest, allocations, addOnOrdersResponse);
  }

  @Override
  public List<ReceptionistRoomResponse> getAvailableRooms(UUID roomTypeId, UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);

    // Fetch directly from DB with hotelId filter to avoid loading unwanted rooms
    // into memory.
    // Also uses @EntityGraph to prevent N+1 on roomType.
    List<Room> rooms =
        roomRepository.findAllByRoomType_IdAndHotel_IdAndStatus(
            roomTypeId, receptionist.getHotel().getId(), RoomStatus.AVAILABLE);

    return rooms.stream().map(receptionistMapper::toReceptionistRoomResponse).toList();
  }

  @Override
  @Transactional
  public void checkIn(UUID bookingId, CheckInRequest request, UUID receptionistId) {
    // 1. Verify Receptionist & Booking Ownership
    Receptionist receptionist = getReceptionist(receptionistId);
    HotelBooking booking = getBooking(bookingId, receptionist.getHotel().getId());

    // 2. Ensure Booking is PAID
    if (booking.getStatus() != BookingStatus.PAID) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Booking must be PAID to check-in");
    }

    // 3. Validate Check-in time (from 12:00 PM on startDate)
    LocalDateTime earliestCheckIn = LocalDateTime.of(booking.getStartDate(), LocalTime.of(12, 0));
    if (LocalDateTime.now().isBefore(earliestCheckIn)) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST,
          "Check-in is only allowed after 12:00 PM on " + booking.getStartDate());
    }

    List<HotelBookingDetail> details =
        hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);

    // 4. Match Requested Keys with Booked Rooms Quantity
    int totalNeededRooms = details.stream().mapToInt(HotelBookingDetail::getQuantity).sum();
    if (request.getRoomIds().size() != totalNeededRooms) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Number of requested rooms does not match booking quantity");
    }

    // 5. Batch Fetch Physical Rooms (Fix N+1)
    List<Room> requestedRooms = roomRepository.findAllById(request.getRoomIds());
    if (requestedRooms.size() != request.getRoomIds().size()) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Some requested rooms do not exist");
    }

    // Group requested rooms by RoomType ID and validate availability
    Map<UUID, List<Room>> roomsByType = new HashMap<>();
    for (Room room : requestedRooms) {
      // Ensure physical room is empty and clean (AVAILABLE)
      if (room.getStatus() != RoomStatus.AVAILABLE) {
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST, "Room " + room.getRoomNumber() + " is not available");
      }
      roomsByType.computeIfAbsent(room.getRoomType().getId(), k -> new ArrayList<>()).add(room);
    }

    List<RoomAssignment> assignmentsToSave = new ArrayList<>();

    // 6. Dynamic Room Assignment & Status Update
    for (HotelBookingDetail detail : details) {
      int needed = detail.getQuantity();
      UUID detailRoomTypeId = detail.getRoomType().getId();
      List<Room> availableForThisType =
          roomsByType.getOrDefault(detailRoomTypeId, new ArrayList<>());
      // Ensure we have enough physical rooms for this specific RoomType
      if (availableForThisType.size() < needed) {
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST,
            "Insufficient rooms provided for room type: " + detail.getRoomType().getName());
      }

      // Assign each physical room to the booking detail
      for (int i = 0; i < needed; i++) {
        Room roomToAssign = availableForThisType.remove(0);

        // Mark room as OCCUPIED (Dirty checking handles DB update)
        roomToAssign.setStatus(RoomStatus.OCCUPIED);

        assignmentsToSave.add(
            RoomAssignment.builder().hotelBookingDetail(detail).room(roomToAssign).build());
      }
    }

    roomAssignmentRepository.saveAll(assignmentsToSave);

    // 7. Complete Check-in Transaction
    booking.setStatus(BookingStatus.CHECKED_IN);
    booking.setActualCheckInTime(LocalDateTime.now());
  }

  @Override
  @Transactional
  public CheckOutResponse checkOut(UUID bookingId, UUID receptionistId) {
    // 1. Verify Receptionist & Booking Ownership
    Receptionist receptionist = getReceptionist(receptionistId);
    HotelBooking booking = getBooking(bookingId, receptionist.getHotel().getId());

    // 2. Ensure Booking is CHECKED_IN
    if (booking.getStatus() != BookingStatus.CHECKED_IN) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Booking must be CHECKED_IN to check-out");
    }

    // 3. Fetch all Add-on Orders for this booking
    List<AddOnOrder> orders = addOnOrderRepository.findAllByHotelBooking_Id(bookingId);

    // 4. Calculate Add-on Charges (only for DELIVERED orders)
    BigDecimal addOnCharges =
        orders.stream()
            .filter(o -> o.getStatus() == AddOnOrderStatus.DELIVERED)
            .map(AddOnOrder::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // 5. Calculate Late Check-out Fees (20,000 VND per hour after 12:00 PM of
    // endDate)
    LocalDate endDate = booking.getEndDate();
    LocalDateTime checkOutDeadline = LocalDateTime.of(endDate, LocalTime.of(12, 0));
    BigDecimal lateFees = BigDecimal.ZERO;
    LocalDateTime now = LocalDateTime.now();

    if (now.isAfter(checkOutDeadline)) {
      long hoursLate = Duration.between(checkOutDeadline, now).toHours();
      if (hoursLate > 0) {
        lateFees = BigDecimal.valueOf(hoursLate).multiply(BigDecimal.valueOf(20000));
      } else {
        // Charge minimum 1 hour (20,000) if late but less than a full hour
        lateFees = BigDecimal.valueOf(20000);
      }
    }

    // 6. Identify unpaid Add-on Orders (PENDING status) for the bill
    List<AddOnOrderResponse> unpaidAddOns =
        orders.stream()
            .filter(o -> o.getStatus() == AddOnOrderStatus.PENDING)
            .map(receptionistMapper::toAddOnOrderResponse)
            .toList();

    // 7. Generate Final Bill via Mapper
    return receptionistMapper.toCheckOutResponse(
        booking, addOnCharges, lateFees, addOnCharges.add(lateFees), unpaidAddOns);
  }

  @Override
  @Transactional
  public void confirmCheckOut(UUID bookingId, UUID receptionistId) {
    Receptionist receptionist = getReceptionist(receptionistId);
    HotelBooking booking = getBooking(bookingId, receptionist.getHotel().getId());

    if (booking.getStatus() != BookingStatus.CHECKED_IN) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Booking is not CHECKED_IN");
    }

    // Fetch all assignments for this booking in 1 query to prevent N+1
    List<RoomAssignment> assignments =
        roomAssignmentRepository.findAllByHotelBookingDetail_HotelBooking_Id(bookingId);

    // Set all assigned rooms to CLEANING status (Dirty checking will save
    // automatically)
    for (RoomAssignment assignment : assignments) {
      assignment.getRoom().setStatus(RoomStatus.CLEANING);
    }

    // Update booking status
    booking.setStatus(BookingStatus.CHECKED_OUT);
    booking.setActualCheckOutTime(LocalDateTime.now());

    // Notify Tourist to review
    notificationService.sendToUser(
        booking.getUser().getEmail(),
        NotificationType.POST_TOUR_REVIEW,
        "Cảm ơn bạn đã lưu trú!",
        "Chúng tôi hy vọng bạn đã có một kỳ nghỉ tuyệt vời. Hãy để lại đánh giá cho khách sạn nhé!",
        booking.getId().toString());
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
            .findWithServiceById(orderId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Order not found"));

    // Verify hotel
    if (!order.getHotelService().getHotel().getId().equals(receptionist.getHotel().getId())) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Access denied");
    }

    order.setStatus(status);
  }

  /**
   * Retrieves the Receptionist entity by its unique ID.
   *
   * @param id The UUID of the receptionist to retrieve.
   * @return The Receptionist entity.
   * @throws BaseAppException if the receptionist is not found.
   */
  private Receptionist getReceptionist(UUID id) {
    return receptionistRepository
        .findById(id)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Receptionist not found"));
  }

  /**
   * Retrieves the HotelBooking entity and verifies that it belongs to the specified hotel. This
   * acts as a security check to ensure receptionists can only access bookings for their own hotel.
   *
   * @param bookingId The UUID of the booking to retrieve.
   * @param hotelId The UUID of the hotel that the booking should belong to.
   * @return The verified HotelBooking entity.
   * @throws BaseAppException if the booking is not found, or if it does not belong to the specified
   *     hotel.
   */
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
