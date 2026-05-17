package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.GuideTourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.tour.GuideTourInstanceService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
