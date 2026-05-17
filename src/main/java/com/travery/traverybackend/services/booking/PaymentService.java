package com.travery.traverybackend.services.booking;

import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import java.util.UUID;

public interface PaymentService {

  /**
   * Initiate payment for a PENDING booking. Validates ownership, deadline, and creates a
   * PaymentTransaction. Returns a stub payment URL (real gateway integration in a future sprint).
   */
  PaymentInitiationResponse initiatePayment(
      UUID bookingId, InitiatePaymentRequest request, UUID userId);
}
