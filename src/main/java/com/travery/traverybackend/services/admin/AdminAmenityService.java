package com.travery.traverybackend.services.admin;

import com.travery.traverybackend.dtos.request.admin.CreateAmenityRequest;
import com.travery.traverybackend.dtos.response.hotel.AmenityResponse;
import java.util.List;
import java.util.UUID;

public interface AdminAmenityService {
  AmenityResponse createAmenity(CreateAmenityRequest request);

  List<AmenityResponse> getAllAmenities();

  AmenityResponse updateAmenity(UUID amenityId, CreateAmenityRequest request);

  void deleteAmenity(UUID amenityId);
}
