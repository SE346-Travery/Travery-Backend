package com.travery.traverybackend.controllers.tour;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.dtos.response.tour.TourSummaryResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.tour.TourService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tours")
@RequiredArgsConstructor
public class TourController extends AbstractBaseController {

  private final TourService tourService;

  @GetMapping
  public ResponseEntity<SingleResponse<Page<TourSummaryResponse>>> getTours(
      @Valid TourSearchRequest request, @PageableDefault(size = 10) Pageable pageable) {

    Page<TourSummaryResponse> tours = tourService.getTours(request, pageable);
    return success(tours, "Fetched tours successfully");
  }

  @GetMapping("/featured")
  public ResponseEntity<SingleResponse<List<TourSummaryResponse>>> getFeaturedTours() {
    List<TourSummaryResponse> featuredTours = tourService.getFeaturedTours();
    return success(featuredTours, "Fetched featured tours successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<SingleResponse<TourDetailResponse>> getTourDetail(@PathVariable UUID id) {
    TourDetailResponse detail = tourService.getTourDetail(id);
    return success(detail, "Fetched tour detail successfully");
  }

  @GetMapping("/{id}/instances")
  public ResponseEntity<SingleResponse<List<TourInstanceResponse>>> getTourInstances(
      @PathVariable UUID id) {
    List<TourInstanceResponse> instances = tourService.getTourInstances(id);
    return success(instances, "Fetched tour instances successfully");
  }

  @PostMapping("/templates")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<TourResponse>> createTemplate(
      @Valid @RequestBody TourTemplateRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    TourResponse response = tourService.createTemplate(request, userDetails.getUserId());
    return created(response, "Tour template created successfully");
  }

}
