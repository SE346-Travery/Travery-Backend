package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.tour.DestinationResponse;
import com.travery.traverybackend.entities.common.Destination;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface DestinationMapper {
  DestinationResponse toResponse(Destination destination);
}
