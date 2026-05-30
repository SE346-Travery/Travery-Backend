package com.travery.traverybackend.dtos.request.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class HotelSearchRequest {
  private String keyword;
  private String cityProvince;
  private LocalDate startDate;
  private LocalDate endDate;
  private Integer adults;
  private Integer children;
  private Integer minRating;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
}
