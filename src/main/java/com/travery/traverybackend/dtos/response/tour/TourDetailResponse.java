package com.travery.traverybackend.dtos.response.tour;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailResponse {
  private UUID id;
  private String name;
  private String description;
  private BigDecimal pricePerAdult;
  private BigDecimal pricePerChild;
  private Double averageRating;
  private Integer ratingCount;
  private String startLocation;
  private List<TourItineraryResponse> itineraryList;
  private List<ImageResponse> images;
  private DestinationResponse destination;
  private RefundPolicyResponse refundPolicy;
  private Integer durationDays;
}
