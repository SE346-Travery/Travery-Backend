package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.dtos.response.coach.GuideBookingResponse;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.coach.GuideCoachTripService;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guide/coach-trips")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GUIDE')")
public class GuideCoachTripController extends AbstractBaseController {

  private final GuideCoachTripService guideService;

  /**
   * GET /api/v1/guide/coach-trips
   *
   * <p>Returns a paginated list of trips assigned to the currently authenticated guide.
   *
   * @param status optional filter by trip status
   */
  @GetMapping
  public ResponseEntity<SingleResponse<Page<CoachTripResponse>>> getMyTrips(
      @RequestParam(required = false) CoachTripStatus status,
      @PageableDefault(size = 10, sort = "departureTime", direction = Sort.Direction.ASC)
          Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Page<CoachTripResponse> response =
        guideService.getMyTrips(userDetails.getUserId(), status, pageable);
    return success(response, "Fetched assigned trips successfully");
  }

  /**
   * GET /api/v1/guide/coach-trips/{id}
   *
   * <p>Returns the detail of a specific trip. Only accessible by the guide assigned to that trip.
   */
  @GetMapping("/{id}")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> getTripDetail(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails) {
    CoachTripDetailResponse response =
        guideService.getTripDetail(userDetails.getUserId(), id);
    return success(response, "Fetched trip detail successfully");
  }

  /**
   * GET /api/v1/guide/coach-trips/{id}/bookings
   *
   * <p>Returns the attendance list for a trip. Only includes bookings that have been paid (PAID,
   * CHECKED_IN, NO_SHOW).
   */
  @GetMapping("/{id}/bookings")
  public ResponseEntity<SingleResponse<List<GuideBookingResponse>>> getTripAttendance(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<GuideBookingResponse> response =
        guideService.getTripAttendance(userDetails.getUserId(), id);
    return success(response, "Fetched attendance list successfully");
  }

  /**
   * PUT /api/v1/guide/coach-trips/{id}/status
   *
   * <p>Updates the trip status following the allowed state machine: OPEN/FULL → IN_PROGRESS →
   * COMPLETED. Any other transition will be rejected with 422.
   */
  @PutMapping("/{id}/status")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> updateTripStatus(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateCoachTripStatusRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CoachTripDetailResponse response =
        guideService.updateTripStatus(userDetails.getUserId(), id, request);
    return success(response, "Trip status updated successfully");
  }

  /**
   * PUT /api/v1/guide/coach-trips/{id}/bookings/{bookingId}/check-in
   *
   * <p>Marks the booking representative as present (PAID → CHECKED_IN). Applies to multi-seat
   * bookings as well — only the representative needs to be physically present.
   */
  @PutMapping("/{id}/bookings/{bookingId}/check-in")
  public ResponseEntity<SuccessResponse> checkInBooking(
      @PathVariable UUID id,
      @PathVariable UUID bookingId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    guideService.checkInBooking(userDetails.getUserId(), id, bookingId);
    return success("Booking checked in successfully");
  }

  /**
   * PUT /api/v1/guide/coach-trips/{id}/bookings/{bookingId}/no-show
   *
   * <p>Marks the booking representative as absent (PAID → NO_SHOW). Only allowed when the trip is
   * in IN_PROGRESS status.
   */
  @PutMapping("/{id}/bookings/{bookingId}/no-show")
  public ResponseEntity<SuccessResponse> markPassengerNoShow(
      @PathVariable UUID id,
      @PathVariable UUID bookingId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    guideService.markPassengerNoShow(userDetails.getUserId(), id, bookingId);
    return success("Booking marked as no-show successfully");
  }
}
