package com.travery.traverybackend.controllers.booking;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateAddOnOrderRequest;
import com.travery.traverybackend.dtos.request.booking.CreateHotelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.booking.AddOnBillResponse;
import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelServiceResponse;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.booking.HotelBookingService;
import com.travery.traverybackend.services.booking.PaymentService;
import com.travery.traverybackend.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hotel-bookings")
@RequiredArgsConstructor
public class HotelBookingController extends AbstractBaseController {

  private final HotelBookingService hotelBookingService;
  private final PaymentService paymentService;

  private final RequestUtil requestUtil;

  @PostMapping
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<HotelBookingResponse>> createBooking(
      @Valid @RequestBody CreateHotelBookingRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser,
      HttpServletRequest httpServletRequest) {

    request.setIpAddress(requestUtil.getIpAddress(httpServletRequest));
    HotelBookingResponse response =
        hotelBookingService.createBooking(request, currentUser.getUserId());
    return created(response, "Hotel booking created successfully");
  }

  @GetMapping("/me")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<Page<HotelBookingSummaryResponse>>> getMyBookings(
      @RequestParam(required = false) BookingStatus status,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    Page<HotelBookingSummaryResponse> page =
        hotelBookingService.getMyBookings(currentUser.getUserId(), status, pageable);
    return success(page, "Hotel bookings retrieved successfully");
  }

  @GetMapping("/{bookingId}")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<HotelBookingDetailResponse>> getBookingDetail(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    HotelBookingDetailResponse response =
        hotelBookingService.getBookingDetail(bookingId, currentUser.getUserId());
    return success(response, "Hotel booking detail retrieved successfully");
  }

  @PostMapping("/{bookingId}/cancel")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<CancelBookingResponse>> cancelBooking(
      @PathVariable UUID bookingId,
      @RequestBody(required = false) CancelBookingRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    CancelBookingResponse response =
        hotelBookingService.cancelBooking(bookingId, request, currentUser.getUserId());
    return success(response, "Hotel booking cancelled successfully");
  }

  @PostMapping("/{bookingId}/payments")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<PaymentInitiationResponse>> initiatePayment(
      @PathVariable UUID bookingId,
      @AuthenticationPrincipal CustomUserDetails currentUser,
      HttpServletRequest httpServletRequest) {
    String ipAddress = requestUtil.getIpAddress(httpServletRequest);
    InitiatePaymentRequest request =
        InitiatePaymentRequest.builder().bookingId(bookingId).ipAddress(ipAddress).build();
    PaymentInitiationResponse response =
        paymentService.initiatePayment(
            bookingId, request, currentUser.getUserId(), BookingType.HOTEL_BOOKING);
    return created(response, "Payment initiated successfully");
  }

  @PostMapping("/{bookingId}/add-on-orders")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<AddOnOrderResponse>> createAddOnOrder(
      @PathVariable UUID bookingId,
      @Valid @RequestBody CreateAddOnOrderRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    AddOnOrderResponse response =
        hotelBookingService.createAddOnOrder(bookingId, request, currentUser.getUserId());
    return created(response, "Add-on order created successfully");
  }

  @GetMapping("/{bookingId}/add-on-bill")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<AddOnBillResponse>> getAddOnBill(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    AddOnBillResponse response =
        hotelBookingService.getAddOnBill(bookingId, currentUser.getUserId());
    return success(response, "Current add-on bill retrieved successfully");
  }

  @DeleteMapping("/add-on-orders/{orderId}")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<Void>> cancelAddOnOrder(
      @PathVariable UUID orderId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    hotelBookingService.cancelAddOnOrder(orderId, currentUser.getUserId());
    return success(null, "Add-on order cancelled successfully");
  }

  @GetMapping("/{bookingId}/available-services")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<List<HotelServiceResponse>>> getAvailableServices(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    List<HotelServiceResponse> services =
        hotelBookingService.getAvailableServices(bookingId, currentUser.getUserId());
    return success(services, "Available hotel services retrieved successfully");
  }
}
