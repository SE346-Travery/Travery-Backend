package com.travery.traverybackend.services.hotel.impl;

import com.travery.traverybackend.dtos.request.hotel.CreateAmenityRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateAmenityRequest;
import com.travery.traverybackend.dtos.response.hotel.AmenityResponse;
import com.travery.traverybackend.entities.hotel.Amenity;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.HotelMapper;
import com.travery.traverybackend.repositories.hotel.AmenityRepository;
import com.travery.traverybackend.services.hotel.AdminAmenityService;
import com.travery.traverybackend.services.media.MediaService;
import com.travery.traverybackend.enums.common.CloudinaryFolder;
import java.util.List;
import java.util.Map;
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
  private final MediaService mediaService;

  @Override
  public AmenityResponse createAmenity(CreateAmenityRequest request) {
    if (amenityRepository.existsByName(request.getName())) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Amenity already exists");
    }

    String iconUrl = null;
    String iconPublicId = null;
    if (request.getIconImage() != null && !request.getIconImage().isEmpty()) {
      Map<String, Object> uploadResult = mediaService.uploadImage(request.getIconImage(), CloudinaryFolder.AMENITIES);
      iconUrl = (String) uploadResult.get("url");
      iconPublicId = (String) uploadResult.get("public_id");
    }

    Amenity amenity = Amenity.builder()
        .name(request.getName())
        .type(request.getType())
        .iconUrl(iconUrl)
        .iconPublicId(iconPublicId)
        .build();

    amenity = amenityRepository.save(amenity);
    return hotelMapper.toAmenityResponse(amenity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AmenityResponse> getAllAmenities() {
    return amenityRepository.findAll().stream()
        .filter(Amenity::isActive)
        .map(hotelMapper::toAmenityResponse)
        .toList();
  }

  @Override
  public AmenityResponse updateAmenity(UUID amenityId, UpdateAmenityRequest request) {
    Amenity amenity = amenityRepository
        .findById(amenityId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Amenity not found"));

    if (request.getName() != null) {
      amenity.setName(request.getName());
    }
    if (request.getType() != null) {
      amenity.setType(request.getType());
    }

    if (request.getIconImage() != null && !request.getIconImage().isEmpty()) {
      // Delete old image if it exists
      if (amenity.getIconPublicId() != null && !amenity.getIconPublicId().isEmpty()) {
        mediaService.deleteImage(amenity.getIconPublicId());
      }
      // Upload new image
      Map<String, Object> uploadResult = mediaService.uploadImage(request.getIconImage(), CloudinaryFolder.AMENITIES);
      amenity.setIconUrl((String) uploadResult.get("url"));
      amenity.setIconPublicId((String) uploadResult.get("public_id"));
    }

    amenity = amenityRepository.save(amenity);
    return hotelMapper.toAmenityResponse(amenity);
  }

  @Override
  public void deleteAmenity(UUID amenityId) {
    Amenity amenity = amenityRepository
        .findById(amenityId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Amenity not found"));

    amenity.setActive(false);
    amenityRepository.save(amenity);
  }
}
