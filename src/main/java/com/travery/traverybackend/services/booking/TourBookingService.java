package com.travery.traverybackend.services.booking;

import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateTourBookingRequest;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingSummaryResponse;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TourBookingService {

  TourBookingResponse createBooking(UUID instanceId, CreateTourBookingRequest request, UUID userId);

  Page<TourBookingSummaryResponse> getMyBookings(
      UUID userId, BookingStatus status, Pageable pageable);

  TourBookingDetailResponse getBookingDetail(UUID bookingId, UUID userId);

  CancelBookingResponse cancelBooking(UUID bookingId, CancelBookingRequest request, UUID userId);
}
