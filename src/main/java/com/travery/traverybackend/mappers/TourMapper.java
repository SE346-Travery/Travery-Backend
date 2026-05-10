package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.entities.tour.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TourMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "coordinator", ignore = true)
  @Mapping(target = "hotel", ignore = true)
  @Mapping(target = "destination", ignore = true)
  @Mapping(target = "refundPolicy", ignore = true)
  @Mapping(target = "itineraries", ignore = true)
  @Mapping(target = "requestedByUser", ignore = true)
  @Mapping(source = "isCustom", target = "isCustom")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Tour toEntity(TourTemplateRequest request);

  @Mapping(source = "destination.name", target = "destinationName")
  @Mapping(source = "hotel.name", target = "hotelName")
  @Mapping(source = "custom", target = "isCustom")
  TourResponse toResponse(Tour tour);
}
