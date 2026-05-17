package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.tour.TourIncidentResponse;
import com.travery.traverybackend.entities.tour.TourIncident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TourIncidentMapper {

  @Mapping(source = "tourInstance.id", target = "tourInstanceId")
  @Mapping(source = "reporter.id", target = "reporterId")
  @Mapping(source = "reporter.fullName", target = "reporterName")
  TourIncidentResponse toResponse(TourIncident incident);
}
