package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.hotel.AmenityResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelBasicResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelDetailResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelResponse;
import com.travery.traverybackend.dtos.response.hotel.RoomTypeResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelServiceResponse;
import com.travery.traverybackend.entities.hotel.Amenity;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.hotel.HotelService;
import com.travery.traverybackend.entities.hotel.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {RefundPolicyMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelMapper {

  @Mapping(target = "minPrice", ignore = true) // Set in service
  @Mapping(target = "thumbnailUrl", ignore = true) // Set in service
  HotelResponse toHotelResponse(Hotel hotel);

  @Mapping(target = "amenities", source = "amenities")
  @Mapping(target = "roomTypes", ignore = true) // Set in service to avoid N+1 and redundancy
  @Mapping(target = "images", ignore = true) // Set in service
  HotelDetailResponse toHotelDetailResponse(Hotel hotel);

  @Mapping(target = "amenities", source = "amenities")
  HotelBasicResponse toHotelBasicResponse(Hotel hotel);

  @Mapping(target = "amenities", source = "amenities")
  @Mapping(target = "images", ignore = true) // Set in service
  RoomTypeResponse toRoomTypeResponse(RoomType roomType);

  AmenityResponse toAmenityResponse(Amenity amenity);

  HotelServiceResponse toHotelServiceResponse(HotelService service);
}
