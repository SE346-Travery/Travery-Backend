package com.travery.traverybackend.dtos.request.hotel;

import com.travery.traverybackend.enums.hotel.ServiceCategory;
import java.math.BigDecimal;
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
public class UpdateHotelServiceRequest {
  private String name;

  private ServiceCategory category;

  private BigDecimal price;

  private String unit;

  private String description;

  private Boolean isActive;
}
