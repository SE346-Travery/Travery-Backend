package com.travery.traverybackend.services.admin.impl;

import com.travery.traverybackend.dtos.request.admin.CreateAmenityRequest;
import com.travery.traverybackend.dtos.response.hotel.AmenityResponse;
import com.travery.traverybackend.entities.hotel.Amenity;
import com.travery.traverybackend.enums.hotel.AmenityType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.HotelMapper;
import com.travery.traverybackend.repositories.hotel.AmenityRepository;
import com.travery.traverybackend.services.admin.AdminAmenityService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAmenityServiceImpl implements AdminAmenityService {

  private final AmenityRepository amenityRepository;
  private final HotelMapper hotelMapper;

  @Override
  public AmenityResponse createAmenity(CreateAmenityRequest request) {
    if (amenityRepository.existsByName(request.getName())) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Amenity already exists");
    }

    Amenity amenity =
        Amenity.builder()
            .name(request.getName())
            .type(AmenityType.valueOf(request.getType().toUpperCase()))
            .iconUrl(request.getIconUrl())
            .build();

    amenity = amenityRepository.save(amenity);
    return hotelMapper.toAmenityResponse(amenity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AmenityResponse> getAllAmenities() {
    return amenityRepository.findAll().stream().map(hotelMapper::toAmenityResponse).toList();
  }

  @Override
  public AmenityResponse updateAmenity(UUID amenityId, CreateAmenityRequest request) {
    Amenity amenity =
        amenityRepository
            .findById(amenityId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Amenity not found"));

    amenity.setName(request.getName());
    amenity.setType(AmenityType.valueOf(request.getType().toUpperCase()));
    amenity.setIconUrl(request.getIconUrl());

    amenity = amenityRepository.save(amenity);
    return hotelMapper.toAmenityResponse(amenity);
  }

  @Override
  public void deleteAmenity(UUID amenityId) {
    if (!amenityRepository.existsById(amenityId)) {
      throw new BaseAppException(WebErrorCode.NOT_FOUND, "Amenity not found");
    }
    // Note: This might fail if used by hotels (foreign key constraint)
    amenityRepository.deleteById(amenityId);
  }
}
