package com.travery.traverybackend.controllers.tour;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.tour.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
public class TourController extends AbstractBaseController {

  private final TourService tourService;

  @PostMapping("/templates")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<TourResponse>> createTemplate(
      @Valid @RequestBody TourTemplateRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    TourResponse response = tourService.createTemplate(request, userDetails.getUserId());
    return created(response, "Tour template created successfully");
  }
}
