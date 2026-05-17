package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class TourBookingSummaryResponse {
  private UUID id;
  private BookingStatus status;
  private BigDecimal totalPrice;
  private LocalDateTime paymentDeadline;
  private int memberCount;

  // Tour instance summary
  private String tourName;
  private LocalDate startDate;
  private LocalDate endDate;

  private LocalDateTime createdAt;
}
