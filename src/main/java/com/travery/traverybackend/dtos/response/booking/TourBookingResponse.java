package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class TourBookingResponse {
  private UUID id;

  private String customerName;
  private String customerPhone;
  private String specialRequests;
  private BookingStatus status;

  private BigDecimal totalPrice;
  private BigDecimal pricePerAdultAtBooking;
  private BigDecimal pricePerChildAtBooking;
  private LocalDateTime paymentDeadline;

  // Tour instance summary
  private String tourName;
  private LocalDate startDate;
  private LocalDate endDate;

  private List<BookingMemberResponse> members;

  private PaymentInitiationResponse payment;
}
