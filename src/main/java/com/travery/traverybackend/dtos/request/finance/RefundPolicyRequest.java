package com.travery.traverybackend.dtos.request.finance;

import com.travery.traverybackend.enums.finance.RefundServiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
public class RefundPolicyRequest {

  @NotBlank(message = "Policy name is required")
  private String name;

  @NotNull(message = "Service type is required")
  private RefundServiceType serviceType;

  @NotEmpty(message = "Rules cannot be empty")
  @Valid
  private List<RefundPolicyRuleRequest> rules;
}
