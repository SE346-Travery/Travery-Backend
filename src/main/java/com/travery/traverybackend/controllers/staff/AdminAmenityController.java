package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.hotel.CreateAmenityRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateAmenityRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.hotel.AmenityResponse;
import com.travery.traverybackend.services.hotel.AdminAmenityService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/amenities")
@RequiredArgsConstructor
public class AdminAmenityController extends AbstractBaseController {

  private final AdminAmenityService adminAmenityService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<AmenityResponse>> createAmenity(
      @Valid @ModelAttribute CreateAmenityRequest request) {
    AmenityResponse response = adminAmenityService.createAmenity(request);
    return created(response, "Amenity created successfully");
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<AmenityResponse>>> getAllAmenities() {
    List<AmenityResponse> response = adminAmenityService.getAllAmenities();
    return success(response, "Amenities retrieved successfully");
  }

  @PatchMapping(value = "/{amenityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<AmenityResponse>> updateAmenity(
      @PathVariable UUID amenityId, @Valid @ModelAttribute UpdateAmenityRequest request) {
    AmenityResponse response = adminAmenityService.updateAmenity(amenityId, request);
    return success(response, "Amenity updated successfully");
  }

  @DeleteMapping("/{amenityId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteAmenity(@PathVariable UUID amenityId) {
    adminAmenityService.deleteAmenity(amenityId);
    return success("Amenity deleted successfully");
  }
}
