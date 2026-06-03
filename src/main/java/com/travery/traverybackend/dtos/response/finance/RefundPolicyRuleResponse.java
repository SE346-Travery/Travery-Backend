package com.travery.traverybackend.dtos.response.finance;

import com.travery.traverybackend.enums.finance.RefundTimeUnit;
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
public class RefundPolicyRuleResponse {

  private UUID id;
  private Integer timeBefore;
  private RefundTimeUnit timeUnit;
  private BigDecimal refundPercentage;
}
