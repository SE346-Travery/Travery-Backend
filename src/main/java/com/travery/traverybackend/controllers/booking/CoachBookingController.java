package com.travery.traverybackend.controllers.booking;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateCoachBookingRequest;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.CoachBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.CoachBookingResponse;
import com.travery.traverybackend.dtos.response.booking.CoachBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.booking.CoachBookingService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coach-bookings")
@RequiredArgsConstructor
public class CoachBookingController extends AbstractBaseController {

  private final CoachBookingService coachBookingService;
  private final RequestUtil requestUtil;

  @PostMapping
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<CoachBookingResponse>> createBooking(
      @Valid @RequestBody CreateCoachBookingRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      HttpServletRequest httpRequest) {
    String ipAddress = requestUtil.getIpAddress(httpRequest);
    CoachBookingResponse response =
        coachBookingService.createBooking(request, userDetails.getUserId(), ipAddress);
    return created(response, "Coach booking created successfully");
  }

  @PostMapping("/{bookingId}/payment")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<PaymentInitiationResponse>> generatePaymentUrl(
      @PathVariable UUID bookingId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      HttpServletRequest httpRequest) {

    String ipAddress = requestUtil.getIpAddress(httpRequest);
    InitiatePaymentRequest request =
        InitiatePaymentRequest.builder().bookingId(bookingId).ipAddress(ipAddress).build();

    PaymentInitiationResponse response =
        coachBookingService.generatePaymentUrl(bookingId, request, userDetails.getUserId());
    return success(response, "Payment URL generated successfully");
  }

  @GetMapping
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<Page<CoachBookingSummaryResponse>>> getMyBookings(
      @RequestParam(required = false) BookingStatus status,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    var response = coachBookingService.getMyBookings(userDetails.getUserId(), status, pageable);
    return success(response, "Fetched my bookings successfully");
  }

  @GetMapping("/{bookingId}")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<CoachBookingDetailResponse>> getBookingDetail(
      @PathVariable UUID bookingId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    var response = coachBookingService.getBookingDetail(bookingId, userDetails.getUserId());
    return success(response, "Coach booking detail retrieved successfully");
  }

  @PostMapping("/{bookingId}/cancel")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<CancelBookingResponse>> cancelBooking(
      @PathVariable UUID bookingId,
      @RequestBody(required = false) CancelBookingRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    var response = coachBookingService.cancelBooking(bookingId, request, userDetails.getUserId());
    return success(response, "Coach booking cancelled successfully");
  }
}
