package com.travery.traverybackend.dtos.response.staff;

import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import java.math.BigDecimal;
import java.util.List;
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
public class CheckOutResponse {
  private UUID bookingId;
  private BigDecimal roomCharges;
  private BigDecimal addOnCharges;
  private BigDecimal lateFees;
  private BigDecimal totalBill;
  private List<AddOnOrderResponse> unpaidAddOns;
}
