package com.travery.traverybackend.dtos.response.finance;

import com.travery.traverybackend.enums.booking.BookingType;
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
public class RefundRequestResponse {
  private UUID id;
  private UUID userId;
  private String userName;
  private String userEmail;
  private BigDecimal requestedAmount;
  private BigDecimal actualRefunded;
  private String customerReason;
  private String rejectReason;

  private BookingType bookingType;

  private String bankName;
  private String accountNumber;
  private String accountHolderName;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
