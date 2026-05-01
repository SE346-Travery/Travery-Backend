package com.travery.traverybackend.dtos.request.tour;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateTourRequest {
  @NotBlank(message = "Tour name is required")
  private String name;

  private String description;

  @NotNull(message = "Price per adult is required")
  @Positive(message = "Price must be positive")
  private BigDecimal pricePerAdult;

  @NotNull(message = "Price per child is required")
  @Positive(message = "Price must be positive")
  private BigDecimal pricePerChild;

  @Positive(message = "Max capacity must be positive")
  private int maxCapacity;

  @Positive(message = "Min capacity must be positive")
  private int minCapacity;

  private boolean isCustom;

  private UUID hotelId;

  @Valid
  private List<TourItineraryDayRequest> itinerary;

  private List<String> imageUrls;
}
