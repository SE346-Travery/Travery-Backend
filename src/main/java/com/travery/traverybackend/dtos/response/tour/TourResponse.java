package com.travery.traverybackend.dtos.response.tour;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourResponse {
  private UUID id;
  private String name;
  private String description;
  private BigDecimal pricePerAdult;
  private BigDecimal pricePerChild;
  private int maxCapacity;
  private int minCapacity;
  private boolean isCustom;
  private String status;
  private List<TourItineraryDayResponse> itinerary;
  private List<String> imageUrls;
}
