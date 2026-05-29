package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachBookingResponse {
  private UUID id;
  private UUID tripId;
  private LocalDateTime departureTime;
  private String originDestination;
  private String destinationDestination;
  private BigDecimal basePrice;
  private BigDecimal totalPrice;
  private LocalDateTime paymentDeadline;
  private String contactName;
  private String contactPhone;
  private BookingStatus status;
  private List<String> bookedSeatNames;

  // Payment URL if generated during booking creation
  private PaymentInitiationResponse payment;
}
