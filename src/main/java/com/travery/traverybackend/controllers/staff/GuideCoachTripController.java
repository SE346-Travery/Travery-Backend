package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.coach.GuideCoachAttendanceRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.coach.GuideCoachTripService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

  @GetMapping
  public ResponseEntity<SingleResponse<List<CoachTripResponse>>> getAssignedTrips(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(defaultValue = "all") String filter) {
    List<CoachTripResponse> trips = guideService.getAssignedTrips(userDetails.getUserId(), filter);
    return success(trips, "Fetched assigned coach trips successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> getTripDetail(
      @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID id) {
    CoachTripDetailResponse detail = guideService.getTripDetail(userDetails.getUserId(), id);
    return success(detail, "Fetched assigned coach trip detail successfully");
  }

  @PatchMapping("/{id}/attendance")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> recordAttendance(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable UUID id,
      @Valid @RequestBody GuideCoachAttendanceRequest request) {
    CoachTripDetailResponse response =
        guideService.recordAttendance(userDetails.getUserId(), id, request);
    return success(response, "Recorded coach trip attendance successfully");
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> updateTripStatus(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable UUID id,
      @Valid @RequestBody UpdateCoachTripStatusRequest request) {
    CoachTripDetailResponse response =
        guideService.updateTripStatus(userDetails.getUserId(), id, request);
    return success(response, "Update coach trip status successfully");
  }
}
