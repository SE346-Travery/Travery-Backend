package com.travery.traverybackend.dtos.response.hotel;

import com.travery.traverybackend.enums.hotel.ServiceCategory;
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
  private ServiceCategory category;
  private BigDecimal price;
  private String unit;
  private String description;
  private boolean isActive;
}
