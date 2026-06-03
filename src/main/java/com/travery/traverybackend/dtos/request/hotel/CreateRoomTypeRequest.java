package com.travery.traverybackend.dtos.request.hotel;

import com.travery.traverybackend.enums.hotel.BedType;
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
  @NotBlank
  private String name;
  private String description;

  @Min(1)
  private int capacityAdults;

  @Min(0)
  private int capacityChildren;

  @NotNull
  private BigDecimal basePrice;
  @NotNull
  private BedType bedType;

  @Min(1)
  private int area;
}
