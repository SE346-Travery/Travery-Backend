package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.tour.ImageResponse;
import com.travery.traverybackend.dtos.response.tour.TourDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.dtos.response.tour.TourItineraryResponse;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.dtos.response.tour.TourSummaryResponse;
import com.travery.traverybackend.entities.common.Image;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.tour.TourItinerary;
import com.travery.traverybackend.enums.common.ImageType;
import com.travery.traverybackend.repositories.common.ImageRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    builder = @org.mapstruct.Builder(disableBuilder = true),
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public abstract class TourMapper {



  @Mapping(target = "destinationName", source = "destination.name")
  @Mapping(target = "price", source = "pricePerAdult")
  @Mapping(target = "thumbnailUrl", ignore = true) // Cập nhật qua batch fetching ở Service
  public abstract TourSummaryResponse toTourSummaryResponse(Tour tour);

  @Mapping(target = "images", ignore = true)
  @Mapping(target = "startLocation", source = "pickupLocation")
  @Mapping(target = "ratingCount", ignore = true) // Chưa có rating count
  @Mapping(target = "itineraryList", source = "itineraries")
  public abstract TourDetailResponse toTourDetailResponse(Tour tour);

  @Mapping(
      target = "availableSlots",
      ignore = true) // Cần logic tính availableSlots nếu chưa có sẵn trong DB
  public abstract TourInstanceResponse toTourInstanceResponse(TourInstance instance);

  @Mapping(
      target = "images",
      ignore = true) // Hình ảnh sẽ được map sau hoặc bỏ qua nếu không cần thiết
  public abstract TourItineraryResponse toTourItineraryResponse(TourItinerary itinerary);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "coordinator", ignore = true)
  @Mapping(target = "hotel", ignore = true)
  @Mapping(target = "destination", ignore = true)
  @Mapping(target = "refundPolicy", ignore = true)
  @Mapping(target = "itineraries", ignore = true)
  @Mapping(target = "requestedByUser", ignore = true)
  @Mapping(source = "isCustom", target = "custom")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract Tour toEntity(TourTemplateRequest request);

  @Mapping(source = "destination.name", target = "destinationName")
  @Mapping(source = "hotel.name", target = "hotelName")
  @Mapping(source = "custom", target = "custom")
  public abstract TourResponse toTourResponse(Tour tour);



  public ImageResponse toImageResponse(Image image) {
    if (image == null) return null;
    ImageResponse response = new ImageResponse();
    response.setUrl(image.getUrl());
    response.setIsThumnail(image.isThumbnail());
    return response;
  }
}
