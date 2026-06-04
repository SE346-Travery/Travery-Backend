package com.travery.traverybackend.dtos.request.booking;

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
public class CancelBookingRequest {

  private String reason;
  private String bankName;
  private String accountNumber;
  private String accountHolderName;
}
