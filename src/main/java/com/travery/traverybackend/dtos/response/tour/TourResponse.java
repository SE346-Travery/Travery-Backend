package com.travery.traverybackend.dtos.response.tour;

import java.math.BigDecimal;
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
public class TourResponse {
  private UUID id;
  private String name;
  private String description;
  private String destinationName;
  private String hotelName;
  private String pickupLocation;
  private BigDecimal pricePerAdult;
  private BigDecimal pricePerChild;
  private boolean isCustom;
  private int minParticipants;
  private int maxParticipants;
}
