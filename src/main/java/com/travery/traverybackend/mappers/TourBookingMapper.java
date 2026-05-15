package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.request.booking.BookingMemberRequest;
import com.travery.traverybackend.dtos.response.booking.BookingMemberResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingSummaryResponse;
import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TourBookingMapper {

  BookingMemberResponse toBookingMemberResponse(BookingMember member);

  List<BookingMemberResponse> toBookingMemberResponseList(List<BookingMember> members);

  @Mapping(target = "tourName", source = "booking.tourInstance.tour.name")
  @Mapping(target = "startDate", source = "booking.tourInstance.startDate")
  @Mapping(target = "endDate", source = "booking.tourInstance.endDate")
  @Mapping(target = "members", source = "members")
  TourBookingResponse toTourBookingResponse(TourBooking booking, List<BookingMember> members);

  @Mapping(target = "tourName", source = "booking.tourInstance.tour.name")
  @Mapping(target = "startDate", source = "booking.tourInstance.startDate")
  @Mapping(target = "endDate", source = "booking.tourInstance.endDate")
  @Mapping(target = "memberCount", source = "memberCount")
  TourBookingSummaryResponse toTourBookingSummaryResponse(TourBooking booking, int memberCount);

  @Mapping(target = "id", source = "booking.id")
  @Mapping(target = "status", source = "booking.status")
  @Mapping(target = "createdAt", source = "booking.createdAt")
  @Mapping(target = "tourName", source = "booking.tourInstance.tour.name")
  @Mapping(target = "startDate", source = "booking.tourInstance.startDate")
  @Mapping(target = "endDate", source = "booking.tourInstance.endDate")
  @Mapping(target = "members", source = "members")
  @Mapping(target = "paymentMethod", source = "payment.paymentMethod")
  @Mapping(target = "paymentStatus", source = "payment.status")
  @Mapping(target = "transactionId", source = "payment.id")
  TourBookingDetailResponse toTourBookingDetailResponse(
      TourBooking booking, List<BookingMember> members, PaymentTransaction payment);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "bookingId", ignore = true)
  @Mapping(target = "bookingType", ignore = true)
  BookingMember toBookingMember(BookingMemberRequest request);
}
