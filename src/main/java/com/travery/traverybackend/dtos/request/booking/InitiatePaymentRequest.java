package com.travery.traverybackend.dtos.request.booking;

import com.travery.traverybackend.enums.finance.PaymentMethod;
import jakarta.validation.constraints.NotNull;
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
public class InitiatePaymentRequest {

  @NotNull(message = "Payment method is required")
  private PaymentMethod paymentMethod;
}
