package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingDetailItemResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelServiceResponse;
import com.travery.traverybackend.entities.booking.AddOnOrder;
import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.HotelBookingDetail;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.entities.hotel.HotelService;
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

  @Mapping(target = "id", source = "booking.id")
  @Mapping(target = "status", source = "booking.status")
  @Mapping(target = "createdAt", source = "booking.createdAt")
  @Mapping(target = "hotelName", ignore = true) // Set in service
  @Mapping(target = "hotelAddress", ignore = true) // Set in service
  @Mapping(target = "startDate", source = "booking.startDate")
  @Mapping(target = "endDate", source = "booking.endDate")
  @Mapping(target = "items", source = "details")
  @Mapping(target = "members", source = "members")
  @Mapping(target = "paymentMethod", source = "payment.paymentMethod")
  @Mapping(target = "paymentStatus", source = "payment.status")
  @Mapping(target = "transactionId", source = "payment.id")
  @Mapping(target = "gatewayTransactionId", source = "payment.transactionReference")
  HotelBookingDetailResponse toHotelBookingDetailResponse(
      HotelBooking booking,
      List<HotelBookingDetail> details,
      List<BookingMember> members,
      PaymentTransaction payment);

  @Mapping(target = "roomTypeId", source = "roomType.id")
  @Mapping(target = "roomTypeName", source = "roomType.name")
  HotelBookingDetailItemResponse toHotelBookingDetailItemResponse(HotelBookingDetail detail);

  @Mapping(source = "hotelService.name", target = "serviceName")
  @Mapping(source = "hotelService.category", target = "category")
  @Mapping(source = "hotelService.price", target = "unitPrice")
  AddOnOrderResponse toAddOnOrderResponse(AddOnOrder order);

  HotelServiceResponse toHotelServiceResponse(HotelService service);
}
