package com.travery.traverybackend.dtos.response.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class HotelBookingDetailItemResponse {
  private UUID id;
  private UUID roomTypeId;
  private String roomTypeName;
  private int quantity;
  private BigDecimal priceAtBooking;
  private LocalDate startDate;
  private LocalDate endDate;
}
