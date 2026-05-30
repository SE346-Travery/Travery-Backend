package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelBookingSummaryResponse {
  private UUID id;
  private String hotelName;
  private BigDecimal totalPrice;
  private LocalDateTime paymentDeadline;
  private BookingStatus status;
  private LocalDateTime createdAt;
  private int guestCount;
}
