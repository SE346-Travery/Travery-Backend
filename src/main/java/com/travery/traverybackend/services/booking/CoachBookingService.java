package com.travery.traverybackend.services.booking;

import com.travery.traverybackend.dtos.request.booking.CancelBookingRequest;
import com.travery.traverybackend.dtos.request.booking.CreateCoachBookingRequest;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.CancelBookingResponse;
import com.travery.traverybackend.dtos.response.booking.CoachBookingDetailResponse;
import com.travery.traverybackend.dtos.response.booking.CoachBookingResponse;
import com.travery.traverybackend.dtos.response.booking.CoachBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoachBookingService {

        CoachBookingResponse createBooking(
                        CreateCoachBookingRequest request, UUID userId, String ipAddress);

        PaymentInitiationResponse generatePaymentUrl(
                        UUID bookingId, InitiatePaymentRequest request, UUID userId);

        Page<CoachBookingSummaryResponse> getMyBookings(
                        UUID userId, BookingStatus status, Pageable pageable);

        CoachBookingDetailResponse getBookingDetail(UUID bookingId, UUID userId);

        CancelBookingResponse cancelBooking(UUID bookingId, CancelBookingRequest request, UUID userId);
}
