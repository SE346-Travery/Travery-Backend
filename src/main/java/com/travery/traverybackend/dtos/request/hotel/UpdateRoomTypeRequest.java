package com.travery.traverybackend.dtos.request.hotel;

import com.travery.traverybackend.enums.hotel.BedType;
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
public class UpdateRoomTypeRequest {
  private String name;

  private String description;

  private Integer capacityAdults;

  private Integer capacityChildren;

  private BigDecimal basePrice;

  private BedType bedType;

  private Integer area;
}
