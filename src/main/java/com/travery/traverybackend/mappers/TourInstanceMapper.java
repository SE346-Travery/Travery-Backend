package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.request.tour.TourInstanceCreateRequest;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.TourInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TourInstanceMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tour", ignore = true)
  @Mapping(target = "coordinator", ignore = true)
  @Mapping(target = "guide", ignore = true)
  @Mapping(target = "coach", ignore = true)
  @Mapping(target = "driver", ignore = true)
  @Mapping(target = "hotelBooking", ignore = true)
  @Mapping(target = "currentParticipants", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  TourInstance toEntity(TourInstanceCreateRequest request);

  @Mapping(source = "tour.name", target = "tourName")
  TourInstanceResponse toTourInstanceResponse(TourInstance tourInstance);

  @Mapping(source = "tour.name", target = "tourName")
  @Mapping(source = "tour.destination.name", target = "destinationName")
  @Mapping(source = "tour.pickupLocation", target = "pickupLocation")
  @Mapping(source = "guide.id", target = "guideId")
  @Mapping(source = "guide.fullName", target = "guideName")
  @Mapping(source = "guide.phoneNumber", target = "guidePhone")
  @Mapping(source = "coach.id", target = "coachId")
  @Mapping(source = "coach.licensePlate", target = "coachLicensePlate")
  @Mapping(source = "coach.coachType", target = "coachType")
  @Mapping(source = "driver.id", target = "driverId")
  @Mapping(source = "driver.fullName", target = "driverName")
  @Mapping(source = "driver.phoneNumber", target = "driverPhone")
  TourInstanceDetailResponse toTourInstanceDetailResponse(TourInstance tourInstance);
}
