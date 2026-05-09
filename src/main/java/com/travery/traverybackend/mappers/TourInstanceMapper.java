package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.TourInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TourInstanceMapper {

  @Mapping(source = "tour.name", target = "tourName")
  TourInstanceResponse toTourInstanceResponse(TourInstance tourInstance);
}
