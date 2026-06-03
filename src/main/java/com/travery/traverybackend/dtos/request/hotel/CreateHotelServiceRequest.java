package com.travery.traverybackend.dtos.request.hotel;

import com.travery.traverybackend.enums.hotel.ServiceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateHotelServiceRequest {
  @NotBlank private String name;

  @NotNull private ServiceCategory category;

  @NotNull private BigDecimal price;

  @NotBlank private String unit;

  private String description;
}
