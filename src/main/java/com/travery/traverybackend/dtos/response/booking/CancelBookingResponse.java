package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.finance.RefundStatus;
import java.math.BigDecimal;
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
public class CancelBookingResponse {
  private UUID bookingId;
  private BookingStatus bookingStatus;

  // Refund info (null if booking was PENDING — no payment was made)
  private BigDecimal refundAmount;
  private BigDecimal refundPercentage;
  private RefundStatus refundStatus;
  private String refundMessage;
}
