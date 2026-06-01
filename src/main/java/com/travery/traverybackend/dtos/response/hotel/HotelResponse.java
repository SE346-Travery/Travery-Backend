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
public class HotelResponse {
  private UUID id;
  private String name;
  private String address;
  private String cityProvince;
  private BigDecimal minPrice;
  private Integer averageRating;
  private String thumbnailUrl;
}
