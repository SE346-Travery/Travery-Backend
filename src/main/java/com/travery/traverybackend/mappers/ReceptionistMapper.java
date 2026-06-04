package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelServiceResponse;
import com.travery.traverybackend.dtos.response.staff.CheckOutResponse;
import com.travery.traverybackend.dtos.response.staff.HotelGuestResponse;
import com.travery.traverybackend.dtos.response.staff.ReceptionistBookingDetailResponse;
import com.travery.traverybackend.dtos.response.staff.ReceptionistRoomResponse;
import com.travery.traverybackend.dtos.response.staff.RoomAllocationResponse;
import com.travery.traverybackend.entities.booking.AddOnOrder;
import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.HotelBookingDetail;
import com.travery.traverybackend.entities.hotel.HotelService;
import com.travery.traverybackend.entities.hotel.Room;
import java.math.BigDecimal;
import java.util.List;
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

  @Mapping(target = "id", source = "booking.id")
  @Mapping(target = "guestName", source = "booking.user.fullName")
  @Mapping(target = "phoneNumber", source = "booking.user.phoneNumber")
  @Mapping(target = "checkInDate", source = "booking.startDate")
  @Mapping(target = "checkOutDate", source = "booking.endDate")
  @Mapping(target = "status", source = "booking.status")
  @Mapping(target = "totalPrice", source = "booking.totalPrice")
  @Mapping(target = "totalAddOnCharges", source = "totalAddOnCharges")
  ReceptionistBookingDetailResponse toBookingDetailResponse(
      HotelBooking booking,
      BigDecimal totalAddOnCharges,
      List<HotelGuestResponse> manifest,
      List<RoomAllocationResponse> roomAllocations,
      List<AddOnOrderResponse> addOnOrders);

  HotelGuestResponse toHotelGuestResponse(BookingMember member);

  @Mapping(target = "roomTypeName", source = "detail.roomType.name")
  @Mapping(target = "quantity", source = "detail.quantity")
  @Mapping(target = "assignedRoomNumbers", source = "assignedRoomNumbers")
  RoomAllocationResponse toRoomAllocationResponse(
      HotelBookingDetail detail, List<String> assignedRoomNumbers);

  default CheckOutResponse toCheckOutResponse(
      HotelBooking booking,
      BigDecimal addOnCharges,
      BigDecimal lateFees,
      BigDecimal totalBill,
      List<AddOnOrderResponse> unpaidAddOns) {
    return CheckOutResponse.builder()
        .bookingId(booking.getId())
        .roomCharges(booking.getTotalPrice())
        .addOnCharges(addOnCharges)
        .lateFees(lateFees)
        .totalBill(totalBill)
        .unpaidAddOns(unpaidAddOns)
        .build();
  }
}
