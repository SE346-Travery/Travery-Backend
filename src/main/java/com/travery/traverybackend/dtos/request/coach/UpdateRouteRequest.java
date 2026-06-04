package com.travery.traverybackend.dtos.request.coach;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
public class UpdateRouteRequest {
  private UUID originDestinationId;
  private UUID destinationDestinationId;

  @DecimalMin(value = "0.0", inclusive = false, message = "Distance must be greater than 0")
  @Digits(integer = 4, fraction = 2, message = "Distance must fit numeric(6,2)")
  private BigDecimal distanceKm;

  @DecimalMin(value = "0.0", inclusive = false, message = "Estimated hours must be greater than 0")
  @Digits(integer = 3, fraction = 1, message = "Estimated hours must fit numeric(4,1)")
  private BigDecimal estimatedHours;

  @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
  @Digits(integer = 10, fraction = 2, message = "Base price must fit numeric(12,2)")
  private BigDecimal basePrice;

  private UUID refundPolicyId;
}
