package com.travery.traverybackend.dtos.request.finance;

import com.travery.traverybackend.enums.finance.RefundTimeUnit;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class RefundPolicyRuleRequest {

  @NotNull(message = "Time before is required")
  @Min(value = 0, message = "Time before must be at least 0")
  private Integer timeBefore;

  @NotNull(message = "Time unit is required")
  private RefundTimeUnit timeUnit;

  @NotNull(message = "Refund percentage is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "Refund percentage must be >= 0")
  @DecimalMax(value = "100.0", inclusive = true, message = "Refund percentage must be <= 100")
  private BigDecimal refundPercentage;
}
