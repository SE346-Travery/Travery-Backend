package com.travery.traverybackend.dtos.request.finance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
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
public class ProcessRefundRequest {
  @NotNull(message = "Actual refunded amount is required")
  @PositiveOrZero(message = "Actual refunded amount must be positive or zero")
  private BigDecimal actualRefunded;
}
