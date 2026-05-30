package com.travery.traverybackend.services.hotel;

import com.travery.traverybackend.dtos.request.staff.CheckInRequest;
import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.staff.*;
import com.travery.traverybackend.enums.booking.AddOnOrderStatus;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.hotel.RoomStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReceptionistService {
  ReceptionistDashboardResponse getDashboard(UUID receptionistId);

  Page<ReceptionistBookingSummaryResponse> getBookings(
      UUID receptionistId,
      LocalDate date,
      String guestName,
      BookingStatus status,
      Pageable pageable);

  ReceptionistBookingDetailResponse getBookingDetail(UUID bookingId, UUID receptionistId);

  List<ReceptionistRoomResponse> getAvailableRooms(UUID roomTypeId, UUID receptionistId);

  void checkIn(UUID bookingId, CheckInRequest request, UUID receptionistId);

  CheckOutResponse checkOut(UUID bookingId, UUID receptionistId);

  List<ReceptionistRoomResponse> getAllRooms(UUID receptionistId);

  void updateRoomStatus(UUID roomId, RoomStatus status, UUID receptionistId);

  List<AddOnOrderResponse> getActiveAddOnOrders(UUID receptionistId);

  void updateAddOnOrderStatus(UUID orderId, AddOnOrderStatus status, UUID receptionistId);
}
