package com.travery.traverybackend.controllers.booking;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateReviewRequest;
import com.travery.traverybackend.dtos.request.booking.CreateTourBookingRequest;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.dtos.response.booking.ReviewResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingSummaryResponse;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.booking.PaymentService;
import com.travery.traverybackend.services.booking.ReviewService;
import com.travery.traverybackend.services.booking.TourBookingService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TourBookingController extends AbstractBaseController {

  private final TourBookingService tourBookingService;
  private final PaymentService paymentService;
  private final ReviewService reviewService;
  private final RequestUtil requestUtil;

  @PostMapping("/api/v1/tour-instances/{instanceId}/bookings")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<TourBookingResponse>> createBooking(
      @PathVariable UUID instanceId,
      @Valid @RequestBody CreateTourBookingRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser,
      HttpServletRequest httpServletRequest) {

    var ipAddress = requestUtil.getIpAddress(httpServletRequest);
    request.setIpAddress(ipAddress);
    TourBookingResponse response =
        tourBookingService.createBooking(instanceId, request, currentUser.getUserId());
    return created(response, "Tour booking created successfully");
  }

  @GetMapping("/api/v1/bookings/me")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<Page<TourBookingSummaryResponse>>> getMyBookings(
      @RequestParam(required = false) BookingStatus status,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    Page<TourBookingSummaryResponse> page =
        tourBookingService.getMyBookings(currentUser.getUserId(), status, pageable);
    return success(page, "Bookings retrieved successfully");
  }

  @GetMapping("/api/v1/bookings/{bookingId}")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<TourBookingDetailResponse>> getBookingDetail(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    TourBookingDetailResponse response =
        tourBookingService.getBookingDetail(bookingId, currentUser.getUserId());
    return success(response, "Booking detail retrieved successfully");
  }

  @PostMapping("/api/v1/bookings/{bookingId}/cancel")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<CancelBookingResponse>> cancelBooking(
      @PathVariable UUID bookingId,
      @RequestBody(required = false) CancelBookingRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    CancelBookingResponse response =
        tourBookingService.cancelBooking(bookingId, request, currentUser.getUserId());
    return success(response, "Booking cancelled successfully");
  }

  @PostMapping("/api/v1/bookings/{bookingId}/payments")
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
            bookingId, request, currentUser.getUserId(), BookingType.TOUR_BOOKING);
    return created(response, "Payment initiated successfully");
  }

  @PostMapping("/api/v1/bookings/{bookingId}/reviews")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<ReviewResponse>> createReview(
      @PathVariable UUID bookingId,
      @Valid @RequestBody CreateReviewRequest request,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    ReviewResponse response =
        reviewService.createReview(
            bookingId, request, currentUser.getUserId(), BookingType.TOUR_BOOKING);
    return created(response, "Review submitted successfully");
  }
}
