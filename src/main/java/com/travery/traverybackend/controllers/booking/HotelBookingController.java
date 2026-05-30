package com.travery.traverybackend.controllers.booking;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateAddOnOrderRequest;
import com.travery.traverybackend.dtos.request.booking.CreateHotelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateReviewRequest;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.dtos.response.booking.ReviewResponse;
import com.travery.traverybackend.dtos.response.booking.StayBillResponse;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.booking.HotelBookingService;
import com.travery.traverybackend.services.booking.PaymentService;
import com.travery.traverybackend.services.booking.ReviewService;
import com.travery.traverybackend.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HotelBookingController extends AbstractBaseController {

  private final HotelBookingService hotelBookingService;
  private final PaymentService paymentService;
  private final ReviewService reviewService;
  private final RequestUtil requestUtil;

  @PostMapping("/api/v1/hotel-bookings")
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

  @GetMapping("/api/v1/hotel-bookings/me")
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

  @GetMapping("/api/v1/hotel-bookings/{bookingId}")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<HotelBookingDetailResponse>> getBookingDetail(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    HotelBookingDetailResponse response =
        hotelBookingService.getBookingDetail(bookingId, currentUser.getUserId());
    return success(response, "Hotel booking detail retrieved successfully");
  }

  @GetMapping("/api/v1/hotel-bookings/{bookingId}/cancel-quote")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<CancelBookingResponse>> getCancelQuote(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    CancelBookingResponse response =
        hotelBookingService.getCancelQuote(bookingId, currentUser.getUserId());
    return success(response, "Cancellation quote retrieved successfully");
  }

  @PostMapping("/api/v1/hotel-bookings/{bookingId}/cancel")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<Void>> cancelBooking(
      @PathVariable UUID bookingId,
      @RequestBody(required = false) CancelBookingRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    hotelBookingService.cancelBooking(bookingId, request, currentUser.getUserId());
    return success(null, "Hotel booking cancelled successfully");
  }

  @PostMapping("/api/v1/hotel-bookings/{bookingId}/payments")
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

  @PostMapping("/api/v1/hotel-bookings/{bookingId}/reviews")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<ReviewResponse>> createReview(
      @PathVariable UUID bookingId,
      @Valid @RequestBody CreateReviewRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    ReviewResponse response =
        reviewService.createReview(
            bookingId, request, currentUser.getUserId(), BookingType.HOTEL_BOOKING);
    return created(response, "Review submitted successfully");
  }

  @PostMapping("/api/v1/hotel-bookings/{bookingId}/add-on-orders")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<AddOnOrderResponse>> createAddOnOrder(
      @PathVariable UUID bookingId,
      @Valid @RequestBody CreateAddOnOrderRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    AddOnOrderResponse response =
        hotelBookingService.createAddOnOrder(bookingId, request, currentUser.getUserId());
    return created(response, "Add-on order created successfully");
  }

  @GetMapping("/api/v1/hotel-bookings/{bookingId}/bill")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<StayBillResponse>> getStayBill(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    StayBillResponse response = hotelBookingService.getStayBill(bookingId, currentUser.getUserId());
    return success(response, "Current stay bill retrieved successfully");
  }

  @DeleteMapping("/api/v1/add-on-orders/{orderId}")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<Void>> cancelAddOnOrder(
      @PathVariable UUID orderId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    hotelBookingService.cancelAddOnOrder(orderId, currentUser.getUserId());
    return success(null, "Add-on order cancelled successfully");
  }
}
