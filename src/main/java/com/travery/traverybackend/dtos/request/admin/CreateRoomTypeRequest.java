package com.travery.traverybackend.dtos.request.admin;

import jakarta.validation.constraints.Min;
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
public class CreateRoomTypeRequest {
  @NotBlank private String name;
  private String description;

  @Min(1)
  private int maxAdults;

  @Min(0)
  private int maxChildren;

  @NotNull private BigDecimal basePrice;
  @NotBlank private String bedType;

  @Min(1)
  private int area;

  @Min(1)
  private int quantity;
}
