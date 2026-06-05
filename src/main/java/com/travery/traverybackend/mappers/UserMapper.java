package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.profile.*;
import com.travery.traverybackend.entities.user.*;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  default BaseUserProfileResponse toResponse(User user) {
    if (user == null) {
      return null;
    }

    if (user instanceof Tourist tourist) {
      return toTouristResponse(tourist);
    } else if (user instanceof Guide guide) {
      return toGuideResponse(guide);
    } else if (user instanceof Coordinator coordinator) {
      return toCoordinatorResponse(coordinator);
    } else if (user instanceof Receptionist receptionist) {
      return toReceptionistResponse(receptionist);
    } else {
      return toBaseResponse(user); // For Admin
    }
  }

  BaseUserProfileResponse toBaseResponse(User user);

  TouristProfileResponse toTouristResponse(Tourist tourist);

  GuideProfileResponse toGuideResponse(Guide guide);

  List<GuideProfileResponse> toGuideResponseList(List<Guide> guides);

  CoordinatorProfileResponse toCoordinatorResponse(Coordinator coordinator);

  @Mapping(target = "hotelId", source = "hotel.id")
  @Mapping(target = "hotelName", source = "hotel.name")
  ReceptionistProfileResponse toReceptionistResponse(Receptionist receptionist);
}
