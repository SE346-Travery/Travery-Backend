package com.travery.traverybackend.dtos.request.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class HotelSearchRequest {
  private String keyword;
  private String cityProvince;
  private LocalDate startDate;
  private LocalDate endDate;
  private Integer adults;
  private Integer children;
  private Integer roomCount;
  private Integer minRating;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private List<UUID> amenityIds;

  // Internal field used by the Service layer to pass SQL-filtered available hotels to Lucene
  private List<UUID> availableHotelIds;
}
