package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.services.tour.CoordinatorTourInstanceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff/coordinator/instances")
@RequiredArgsConstructor
public class CoordinatorTourInstanceController {

  private final CoordinatorTourInstanceService coordinatorTourInstanceService;
  private final ResponseFactory responseFactory;

  @GetMapping
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<List<TourInstanceResponse>>> getInstances(
      @RequestParam(defaultValue = "all") String filter) {
    List<TourInstanceResponse> instances = coordinatorTourInstanceService.getInstances(filter);
    return responseFactory.success(instances, "Fetched tour instances successfully");
  }
}
