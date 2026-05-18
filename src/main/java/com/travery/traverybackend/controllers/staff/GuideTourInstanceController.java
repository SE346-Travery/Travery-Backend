package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.tour.GuideAttendanceRequest;
import com.travery.traverybackend.dtos.request.tour.TourIncidentReportRequest;
import com.travery.traverybackend.dtos.request.tour.TourProgressUpdateRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.booking.BookingMemberResponse;
import com.travery.traverybackend.dtos.response.tour.GuideTourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourIncidentResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.tour.GuideTourInstanceService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff/guide/instances")
@RequiredArgsConstructor
public class GuideTourInstanceController extends AbstractBaseController {

  private final GuideTourInstanceService guideTourInstanceService;

  @GetMapping
  @PreAuthorize("hasRole('GUIDE')")
  public ResponseEntity<SingleResponse<List<TourInstanceResponse>>> getAssignedInstances(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(defaultValue = "all") String filter) {
    List<TourInstanceResponse> instances =
        guideTourInstanceService.getAssignedInstances(userDetails.getUserId(), filter);
    return success(instances, "Fetched assigned tour instances successfully");
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('GUIDE')")
  public ResponseEntity<SingleResponse<GuideTourInstanceDetailResponse>> getInstanceDetail(
      @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID id) {
    GuideTourInstanceDetailResponse detail =
        guideTourInstanceService.getInstanceDetail(userDetails.getUserId(), id);
    return success(detail, "Fetched assigned tour instance detail successfully");
  }

  @PatchMapping("/{id}/attendance")
  @PreAuthorize("hasRole('GUIDE')")
  public ResponseEntity<SingleResponse<GuideTourInstanceDetailResponse>> recordAttendance(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable UUID id,
      @Valid @RequestBody GuideAttendanceRequest request) {
    GuideTourInstanceDetailResponse response =
        guideTourInstanceService.recordAttendance(userDetails.getUserId(), id, request);
    return success(response, "Recorded member attendance successfully");
  }

  @GetMapping("/{id}/passengers")
  @PreAuthorize("hasRole('GUIDE')")
  public ResponseEntity<SingleResponse<List<BookingMemberResponse>>> searchPassengers(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable UUID id,
      @RequestParam String query) {
    List<BookingMemberResponse> passengers =
        guideTourInstanceService.searchPassengers(userDetails.getUserId(), id, query);
    return success(passengers, "Searched passengers successfully");
  }

  @PatchMapping("/{id}/progress")
  @PreAuthorize("hasRole('GUIDE')")
  public ResponseEntity<SingleResponse<GuideTourInstanceDetailResponse>> updateProgress(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable UUID id,
      @Valid @RequestBody TourProgressUpdateRequest request) {
    GuideTourInstanceDetailResponse response =
        guideTourInstanceService.updateProgress(userDetails.getUserId(), id, request);
    return success(response, "Updated tour progress successfully");
  }

  @PostMapping("/{id}/incidents")
  @PreAuthorize("hasRole('GUIDE')")
  public ResponseEntity<SingleResponse<TourIncidentResponse>> reportIncident(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable UUID id,
      @Valid @RequestBody TourIncidentReportRequest request) {
    TourIncidentResponse response =
        guideTourInstanceService.reportIncident(userDetails.getUserId(), id, request);
    return success(response, "Reported tour incident successfully");
  }

  @GetMapping("/{id}/incidents")
  @PreAuthorize("hasRole('GUIDE')")
  public ResponseEntity<SingleResponse<List<TourIncidentResponse>>> getIncidents(
      @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID id) {
    List<TourIncidentResponse> response =
        guideTourInstanceService.getIncidents(userDetails.getUserId(), id);
    return success(response, "Fetched tour incidents successfully");
  }
}
