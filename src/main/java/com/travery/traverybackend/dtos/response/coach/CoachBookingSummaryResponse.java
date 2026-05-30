package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachBookingSummaryResponse {
  private UUID id;
  private BookingStatus status;
  private BigDecimal totalPrice;
  private LocalDateTime departureTime;
  private String originDestination;
  private String destinationDestination;
  private int seatCount;
}
