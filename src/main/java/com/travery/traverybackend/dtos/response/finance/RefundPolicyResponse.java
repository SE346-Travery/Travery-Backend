package com.travery.traverybackend.dtos.response.finance;

import com.travery.traverybackend.enums.finance.RefundServiceType;
import java.util.List;
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
public class RefundPolicyResponse {

  private UUID id;
  private String name;
  private RefundServiceType serviceType;
  private List<RefundPolicyRuleResponse> rules;
}
