package com.travery.traverybackend.services.hotel;

import com.travery.traverybackend.dtos.request.hotel.CreateAmenityRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateAmenityRequest;
import com.travery.traverybackend.dtos.response.hotel.AmenityResponse;
import java.util.List;
import java.util.UUID;

public interface AdminAmenityService {
  AmenityResponse createAmenity(CreateAmenityRequest request);

  List<AmenityResponse> getAllAmenities();

  AmenityResponse updateAmenity(UUID amenityId, UpdateAmenityRequest request);

  void deleteAmenity(UUID amenityId);
}
