package com.travery.traverybackend.dtos.response.hotel;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {
  private UUID id;
  private String name;
  private String description;
  private String address;
  private String city;
  private String locationCode;
  private String amenities;
}
