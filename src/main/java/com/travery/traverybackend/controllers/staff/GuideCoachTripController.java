package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.coach.GuideCoachTripService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guide/coach-trips")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GUIDE')")
public class GuideCoachTripController extends AbstractBaseController {

  private final GuideCoachTripService guideService;

  @PutMapping("/{id}/status")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> updateTripStatus(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateCoachTripStatusRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CoachTripDetailResponse response =
        guideService.updateTripStatus(userDetails.getUserId(), id, request);
    return success(response, "Update coach trip status successfully");
  }

  @PutMapping("/{id}/bookings/{bookingId}/no-show")
  public ResponseEntity<SuccessResponse> markPassengerNoShow(
      @PathVariable UUID id,
      @PathVariable UUID bookingId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    guideService.markPassengerNoShow(userDetails.getUserId(), id, bookingId);
    return success("Mark passenger as no-show successfully");
  }
}
