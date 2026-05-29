package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.finance.PaymentMethod;
import com.travery.traverybackend.enums.finance.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachBookingDetailResponse {
  private UUID id;
  private BookingStatus status;
  private BigDecimal basePrice;
  private BigDecimal totalPrice;
  private LocalDateTime paymentDeadline;
  private String contactName;
  private String contactPhone;

  // Trip info
  private UUID tripId;
  private LocalDateTime departureTime;
  private LocalDateTime estimatedArrivalTime;
  private String originDestination;
  private String destinationDestination;
  private String coachLicensePlate;

  // Seats
  private List<String> bookedSeatNames;

  // Payment info
  private PaymentMethod paymentMethod;
  private PaymentStatus paymentStatus;
  private UUID transactionId;
  private String gatewayTransactionId;
}
