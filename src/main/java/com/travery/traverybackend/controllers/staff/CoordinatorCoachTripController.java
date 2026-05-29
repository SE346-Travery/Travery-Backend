package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.coach.CreateCoachTripRequest;
import com.travery.traverybackend.dtos.request.coach.ReassignCoachRequest;
import com.travery.traverybackend.dtos.request.coach.ReassignDriverRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.coach.CoordinatorCoachTripService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coordinator/coach-trips")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COORDINATOR')")
public class CoordinatorCoachTripController extends AbstractBaseController {

  private final CoordinatorCoachTripService coordinatorService;

  @PostMapping
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> createTrip(
      @Valid @RequestBody CreateCoachTripRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CoachTripDetailResponse response =
        coordinatorService.createTrip(request, userDetails.getUserId());
    return success(response, "Create coach trip successfully");
  }

  @GetMapping
  public ResponseEntity<SingleResponse<Page<CoachTripResponse>>> getTrips(
      @RequestParam(required = false) CoachTripStatus status,
      Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Page<CoachTripResponse> response =
        coordinatorService.getTrips(userDetails.getUserId(), status, pageable);
    return success(response, "Get coach trips successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> getTripDetail(
      @PathVariable UUID id) {
    CoachTripDetailResponse response = coordinatorService.getTripDetail(id);
    return success(response, "Get coach trip detail successfully");
  }

  @PutMapping("/{id}/coach")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> reassignCoach(
      @PathVariable UUID id, @Valid @RequestBody ReassignCoachRequest request) {
    CoachTripDetailResponse response = coordinatorService.reassignCoach(id, request.getCoachId());
    return success(response, "Reassign coach successfully");
  }

  @PutMapping("/{id}/driver")
  public ResponseEntity<SingleResponse<CoachTripDetailResponse>> reassignDriver(
      @PathVariable UUID id, @Valid @RequestBody ReassignDriverRequest request) {
    CoachTripDetailResponse response = coordinatorService.reassignDriver(id, request.getDriverId());
    return success(response, "Reassign driver successfully");
  }
}
