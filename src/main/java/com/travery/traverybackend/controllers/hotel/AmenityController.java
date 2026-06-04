package com.travery.traverybackend.controllers.hotel;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.hotel.AmenityResponse;
import com.travery.traverybackend.services.hotel.AdminAmenityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
public class AmenityController extends AbstractBaseController {

  private final AdminAmenityService adminAmenityService;

  @GetMapping
  public ResponseEntity<SingleResponse<List<AmenityResponse>>> getAllAmenities() {
    List<AmenityResponse> response = adminAmenityService.getAllAmenities();
    return success(response, "Amenities retrieved successfully");
  }
}
