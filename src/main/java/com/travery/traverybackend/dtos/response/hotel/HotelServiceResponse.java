package com.travery.traverybackend.dtos.response.hotel;

import java.math.BigDecimal;
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
public class HotelServiceResponse {
  private UUID id;
  private String name;
  private String category;
  private BigDecimal price;
  private String unit;
  private String description;
}
