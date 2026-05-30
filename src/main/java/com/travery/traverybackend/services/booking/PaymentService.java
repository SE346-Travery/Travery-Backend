package com.travery.traverybackend.services.booking;

import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.enums.booking.BookingType;
import java.util.Map;
import java.util.UUID;

public interface PaymentService {

  /**
   * Initiate VNPAY payment for a PENDING booking. Validates ownership, deadline, and creates a
   * PaymentTransaction. Returns a VNPAY payment URL.
   */
  PaymentInitiationResponse initiatePayment(
      UUID bookingId, InitiatePaymentRequest request, UUID userId, BookingType bookingType);

  /**
   * Handle VNPAY IPN callback. Verifies checksum, updates PaymentTransaction and TourBooking
   * status. Returns a map with RspCode and Message for VNPAY.
   */
  Map<String, String> handleVnPayIpn(Map<String, String> params);

  /**
   * Handle VNPAY Return URL redirect. Verifies checksum and builds a mobile deeplink URL for
   * redirecting user back to the app with payment result.
   */
  String handleVnPayReturn(Map<String, String> params);
}
