package com.travery.traverybackend.dtos.response.tour;

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
    private int daysBefore;
    private BigDecimal refundPercentage;
}
