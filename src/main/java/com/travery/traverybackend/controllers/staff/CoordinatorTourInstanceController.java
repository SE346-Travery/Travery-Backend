package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.services.tour.CoordinatorTourInstanceService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<TourInstanceDetailResponse>> getInstanceDetail(
      @PathVariable UUID id) {
    TourInstanceDetailResponse detail = coordinatorTourInstanceService.getInstanceDetail(id);
    return success(detail, "Fetched tour instance detail successfully");
  }
}
