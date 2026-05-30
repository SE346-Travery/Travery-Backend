package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.booking.HotelBookingDetailItemResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.HotelBookingDetail;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {TourBookingMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelBookingMapper {

  @Mapping(target = "userId", source = "booking.user.id")
  @Mapping(target = "members", source = "members")
  @Mapping(target = "payment", source = "payment")
  HotelBookingResponse toHotelBookingResponse(
      HotelBooking booking, List<BookingMember> members, PaymentInitiationResponse payment);

  @Mapping(target = "hotelName", ignore = true) // Set in service
  @Mapping(target = "guestCount", source = "guestCount")
  HotelBookingSummaryResponse toHotelBookingSummaryResponse(HotelBooking booking, int guestCount);

  @Mapping(target = "hotelName", ignore = true) // Set in service
  @Mapping(target = "hotelAddress", ignore = true) // Set in service
  @Mapping(target = "items", source = "details")
  @Mapping(target = "members", source = "members")
  @Mapping(target = "payment", ignore = true) // Set in service
  HotelBookingDetailResponse toHotelBookingDetailResponse(
      HotelBooking booking, List<HotelBookingDetail> details, List<BookingMember> members);

  @Mapping(target = "roomTypeId", source = "roomType.id")
  @Mapping(target = "roomTypeName", source = "roomType.name")
  HotelBookingDetailItemResponse toHotelBookingDetailItemResponse(HotelBookingDetail detail);
}
