package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.staff.CheckInRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.staff.*;
import com.travery.traverybackend.enums.booking.AddOnOrderStatus;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.hotel.RoomStatus;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.hotel.ReceptionistService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/receptionist")
@RequiredArgsConstructor
public class ReceptionistController extends AbstractBaseController {

  private final ReceptionistService receptionistService;

  @GetMapping("/dashboard")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<ReceptionistDashboardResponse>> getDashboard(
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    ReceptionistDashboardResponse response =
        receptionistService.getDashboard(currentUser.getUserId());
    return success(response, "Receptionist dashboard retrieved successfully");
  }

  @GetMapping("/bookings")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<Page<ReceptionistBookingSummaryResponse>>> getBookings(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(required = false) String guestName,
      @RequestParam(required = false) BookingStatus status,
      @PageableDefault(size = 10) Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    Page<ReceptionistBookingSummaryResponse> page =
        receptionistService.getBookings(currentUser.getUserId(), date, guestName, status, pageable);
    return success(page, "Booking queue retrieved successfully");
  }

  @GetMapping("/bookings/{bookingId}")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<ReceptionistBookingDetailResponse>> getBookingDetail(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    ReceptionistBookingDetailResponse response =
        receptionistService.getBookingDetail(bookingId, currentUser.getUserId());
    return success(response, "Booking detail retrieved successfully");
  }

  @GetMapping("/rooms/available")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<List<ReceptionistRoomResponse>>> getAvailableRooms(
      @RequestParam UUID roomTypeId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    List<ReceptionistRoomResponse> rooms =
        receptionistService.getAvailableRooms(roomTypeId, currentUser.getUserId());
    return success(rooms, "Available rooms retrieved successfully");
  }

  @PostMapping("/bookings/{bookingId}/check-in")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<Void>> checkIn(
      @PathVariable UUID bookingId,
      @Valid @RequestBody CheckInRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    receptionistService.checkIn(bookingId, request, currentUser.getUserId());
    return success(null, "Check-in successful");
  }

  @PostMapping("/bookings/{bookingId}/check-out")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<CheckOutResponse>> checkOut(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    CheckOutResponse response = receptionistService.checkOut(bookingId, currentUser.getUserId());
    return success(response, "Check-out successful");
  }

  @GetMapping("/rooms")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<List<ReceptionistRoomResponse>>> getAllRooms(
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    List<ReceptionistRoomResponse> rooms = receptionistService.getAllRooms(currentUser.getUserId());
    return success(rooms, "All rooms retrieved successfully");
  }

  @PatchMapping("/rooms/{roomId}/status")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<Void>> updateRoomStatus(
      @PathVariable UUID roomId,
      @RequestParam RoomStatus status,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    receptionistService.updateRoomStatus(roomId, status, currentUser.getUserId());
    return success(null, "Room status updated successfully");
  }

  @GetMapping("/add-on-orders")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<List<AddOnOrderResponse>>> getActiveAddOnOrders(
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    List<AddOnOrderResponse> orders =
        receptionistService.getActiveAddOnOrders(currentUser.getUserId());
    return success(orders, "Active add-on orders retrieved successfully");
  }

  @PatchMapping("/add-on-orders/{orderId}/status")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public ResponseEntity<SingleResponse<Void>> updateAddOnOrderStatus(
      @PathVariable UUID orderId,
      @RequestParam AddOnOrderStatus status,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    receptionistService.updateAddOnOrderStatus(orderId, status, currentUser.getUserId());
    return success(null, "Add-on order status updated successfully");
  }
}
