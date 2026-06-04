package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.profile.GuideProfileResponse;
import com.travery.traverybackend.services.coach.CoordinatorLookupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coordinator")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COORDINATOR')")
public class CoordinatorLookupController extends AbstractBaseController {

  private final CoordinatorLookupService lookupService;

  @GetMapping("/guides")
  public ResponseEntity<SingleResponse<List<GuideProfileResponse>>> getGuides() {
    List<GuideProfileResponse> response = lookupService.getGuides();
    return success(response, "Fetched guides successfully");
  }
}
