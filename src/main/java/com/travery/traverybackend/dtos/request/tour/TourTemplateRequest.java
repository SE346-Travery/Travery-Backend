package com.travery.traverybackend.dtos.request.tour;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TourTemplateRequest {
  @NotBlank(message = "Tour name is required")
  private String name;

  private String description;

  @NotNull(message = "Destination ID is required")
  private UUID destinationId;

  private UUID hotelId;

  @NotBlank(message = "Pickup location is required")
  private String pickupLocation;

  @NotNull(message = "Price per adult is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "Price per adult must be positive")
  private BigDecimal pricePerAdult;

  @NotNull(message = "Price per child is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "Price per child must be positive")
  private BigDecimal pricePerChild;

  private UUID refundPolicyId;

  private UUID requestedByUserId;

  private Boolean isCustom;

  @NotEmpty(message = "Tour itineraries are required")
  @Valid
  private List<TourItineraryRequest> itineraries;
}
