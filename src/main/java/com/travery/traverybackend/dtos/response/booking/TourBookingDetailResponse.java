package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.finance.PaymentMethod;
import com.travery.traverybackend.enums.finance.PaymentStatus;
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
public class TourBookingDetailResponse {
  private UUID id;
  private BookingStatus status;
  private BigDecimal totalPrice;
  private BigDecimal pricePerAdultAtBooking;
  private BigDecimal pricePerChildAtBooking;
  private LocalDateTime paymentDeadline;
  private String customerNote;
  private LocalDateTime createdAt;

  // Tour instance info
  private String tourName;
  private LocalDate startDate;
  private LocalDate endDate;

  // Members
  private List<BookingMemberResponse> members;

  // Payment info (null if no payment initiated)
  private PaymentMethod paymentMethod;
  private PaymentStatus paymentStatus;
  private UUID transactionId;
}
