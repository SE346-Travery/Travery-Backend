package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelServiceResponse;
import com.travery.traverybackend.dtos.response.staff.ReceptionistRoomResponse;
import com.travery.traverybackend.entities.booking.AddOnOrder;
import com.travery.traverybackend.entities.hotel.HotelService;
import com.travery.traverybackend.entities.hotel.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReceptionistMapper {

  @Mapping(target = "roomTypeName", source = "roomType.name")
  @Mapping(target = "status", source = "status")
  ReceptionistRoomResponse toReceptionistRoomResponse(Room room);

  @Mapping(target = "serviceName", source = "hotelService.name")
  @Mapping(target = "category", source = "hotelService.category")
  @Mapping(target = "unitPrice", source = "hotelService.price")
  @Mapping(target = "status", source = "status")
  AddOnOrderResponse toAddOnOrderResponse(AddOnOrder order);

  @Mapping(target = "category", source = "category")
  HotelServiceResponse toHotelServiceResponse(HotelService service);
}
