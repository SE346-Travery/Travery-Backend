package com.travery.traverybackend.dtos.response.booking;

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
public class StayBillResponse {
  private UUID hotelBookingId;
  private BigDecimal roomCharges;
  private List<AddOnOrderResponse> addOnOrders;
  private BigDecimal totalAddOnCharges;
  private BigDecimal totalBill;
}
