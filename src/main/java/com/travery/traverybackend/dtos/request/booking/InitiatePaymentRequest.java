package com.travery.traverybackend.dtos.request.booking;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Internal DTO used by server to pass payment data from TourBookingService to PaymentService. Not
 * exposed to clients.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiatePaymentRequest {

  private UUID bookingId;

  private BigDecimal amount;

  private String ipAddress;
}
