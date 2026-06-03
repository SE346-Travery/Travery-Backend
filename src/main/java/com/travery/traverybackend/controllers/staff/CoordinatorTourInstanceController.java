package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.tour.TourInstanceCreateRequest;
import com.travery.traverybackend.dtos.request.tour.TourInstanceUpdateRequest;
import com.travery.traverybackend.dtos.request.tour.TourProgressUpdateRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.tour.CoordinatorTourInstanceService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff/coordinator/instances")
@RequiredArgsConstructor
public class CoordinatorTourInstanceController extends AbstractBaseController {

  private final CoordinatorTourInstanceService coordinatorTourInstanceService;

  @GetMapping
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<List<TourInstanceResponse>>> getInstances(
      @RequestParam(defaultValue = "all") String filter) {
    List<TourInstanceResponse> instances = coordinatorTourInstanceService.getInstances(filter);
    return success(instances, "Fetched tour instances successfully");
  }

  @PostMapping
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<TourInstanceDetailResponse>> createInstance(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody TourInstanceCreateRequest request) {
    TourInstanceDetailResponse response =
        coordinatorTourInstanceService.createInstance(request, userDetails.getUserId());
    return created(response, "Tour instance created successfully");
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<TourInstanceDetailResponse>> getInstanceDetail(
      @PathVariable UUID id) {
    TourInstanceDetailResponse response = coordinatorTourInstanceService.getInstanceDetail(id);
    return success(response, "Fetched tour instance detail successfully");
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<TourInstanceDetailResponse>> updateInstance(
      @PathVariable UUID id, @Valid @RequestBody TourInstanceUpdateRequest request) {
    TourInstanceDetailResponse response =
        coordinatorTourInstanceService.updateInstance(id, request);
    return success(response, "Updated tour instance successfully");
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<TourInstanceDetailResponse>> updateStatus(
      @PathVariable UUID id, @Valid @RequestBody TourProgressUpdateRequest request) {
    TourInstanceDetailResponse response = coordinatorTourInstanceService.updateStatus(id, request);
    return success(response, "Updated tour instance status successfully");
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<Void>> deleteInstance(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails) {
    coordinatorTourInstanceService.deleteInstance(id, userDetails.getUserId());
    return success(null, "Tour instance deleted successfully");
  }
}
