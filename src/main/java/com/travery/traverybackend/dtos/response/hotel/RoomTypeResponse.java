package com.travery.traverybackend.dtos.response.hotel;

import com.travery.traverybackend.enums.hotel.BedType;
import java.math.BigDecimal;
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
public class RoomTypeResponse {
  private UUID id;
  private String name;
  private String description;
  private BigDecimal basePrice;
  private int capacityAdults;
  private int capacityChildren;
  private BedType bedType;
  private List<AmenityResponse> amenities;
  private List<HotelImageResponse> images;
}
