package com.travery.traverybackend.dtos.request.hotel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
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
  private Double minRating;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private List<UUID> amenityIds;

  // Internal field: set by Service layer only — NOT bindable from HTTP request
  @JsonIgnore
  @Setter(AccessLevel.NONE)
  private List<UUID> availableHotelIds;

  public void setAvailableHotelIds(List<UUID> availableHotelIds) {
    this.availableHotelIds = availableHotelIds;
  }
}
