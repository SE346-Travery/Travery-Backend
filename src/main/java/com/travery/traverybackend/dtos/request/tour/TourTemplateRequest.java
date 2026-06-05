package com.travery.traverybackend.dtos.request.tour;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
  @Schema(example = "Tour Đà Lạt 3N2Đ")
  @NotBlank(message = "Tour name is required")
  private String name;

  @Schema(example = "Khám phá thành phố ngàn hoa Đà Lạt")
  private String description;

  @Schema(example = "00000000-0000-0000-0000-000000000001")
  @NotNull(message = "Destination ID is required")
  private UUID destinationId;

  private UUID hotelId;

  @Schema(example = "146 Nguyễn Tri Phương, Q.10, TP.HCM")
  @NotBlank(message = "Pickup location is required")
  private String pickupLocation;

  @Schema(example = "2500000")
  @NotNull(message = "Price per adult is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "Price per adult must be positive")
  private BigDecimal pricePerAdult;

  @Schema(example = "1800000")
  @NotNull(message = "Price per child is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "Price per child must be positive")
  private BigDecimal pricePerChild;

  private UUID requestedByUserId;

  private Boolean isCustom;

  @Schema(example = "10")
  @Min(value = 1, message = "Minimum participants must be at least 1")
  private Integer minParticipants;

  @Schema(example = "30")
  @Min(value = 1, message = "Maximum participants must be at least 1")
  private Integer maxParticipants;

  @NotEmpty(message = "Tour itineraries are required")
  @Valid
  private List<TourItineraryRequest> itineraries;
}
