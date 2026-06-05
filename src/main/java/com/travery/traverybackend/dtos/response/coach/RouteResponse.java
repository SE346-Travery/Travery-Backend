package com.travery.traverybackend.dtos.response.coach;

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
public class RouteResponse {
  private UUID id;
  private UUID originDestinationId;
  private String originDestinationName;
  private UUID destinationDestinationId;
  private String destinationDestinationName;
  private BigDecimal distanceKm;
  private BigDecimal estimatedHours;
  private BigDecimal basePrice;
  private UUID refundPolicyId;
  private String refundPolicyName;
  private Double averageRating;
  private Integer reviewCount;
}
