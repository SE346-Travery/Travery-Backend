package com.travery.traverybackend.dtos.response.hotel;

import com.travery.traverybackend.enums.hotel.AmenityType;
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
public class AmenityResponse {
  private UUID id;
  private String name;
  private String iconUrl;
  private AmenityType type;
}
