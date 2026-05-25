package com.travery.traverybackend.controllers.tour;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/tours")
@RequiredArgsConstructor
public class TourController extends AbstractBaseController {

  private final TourService tourService;
  private final ObjectMapper objectMapper;
  private final Validator validator;

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

  @PostMapping(value = "/templates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<TourResponse>> createTemplate(
      @Parameter(schema = @Schema(type = "string", format = "json")) @RequestPart("data")
          String requestJson,
      @RequestPart(value = "tourImages", required = false) List<MultipartFile> tourImages,
      @RequestPart(value = "itineraryImages", required = false) List<MultipartFile> itineraryImages,
      @AuthenticationPrincipal CustomUserDetails userDetails)
      throws Exception {

    TourTemplateRequest request = objectMapper.readValue(requestJson, TourTemplateRequest.class);
    Set<ConstraintViolation<TourTemplateRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }

    TourResponse response =
        tourService.createTemplate(request, tourImages, itineraryImages, userDetails.getUserId());
    return created(response, "Tour template created successfully");
  }
}
