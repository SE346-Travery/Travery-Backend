package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.finance.PaymentMethod;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiationResponse {
  private UUID transactionId;
  private BigDecimal amount;
  private String paymentUrl;
  private LocalDateTime expiresAt;
}
