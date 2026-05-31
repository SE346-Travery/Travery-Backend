package com.travery.traverybackend.services.booking;

import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateAddOnOrderRequest;
import com.travery.traverybackend.dtos.request.booking.CreateHotelBookingRequest;
import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.HotelBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.booking.StayBillResponse;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelBookingService {
  HotelBookingResponse createBooking(CreateHotelBookingRequest request, UUID userId);

  Page<HotelBookingSummaryResponse> getMyBookings(
      UUID userId, BookingStatus status, Pageable pageable);

  HotelBookingDetailResponse getBookingDetail(UUID bookingId, UUID userId);

  CancelBookingResponse getCancelQuote(UUID bookingId, UUID userId);

  void cancelBooking(UUID bookingId, CancelBookingRequest request, UUID userId);

  AddOnOrderResponse createAddOnOrder(UUID bookingId, CreateAddOnOrderRequest request, UUID userId);

  StayBillResponse getStayBill(UUID bookingId, UUID userId);

  void cancelAddOnOrder(UUID orderId, UUID userId);
}
