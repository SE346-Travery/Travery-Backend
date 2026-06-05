package com.travery.traverybackend.dtos.request.tour;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TourItineraryRequest {
  @Schema(example = "1")
  @NotNull(message = "Day number is required")
  @Min(value = 1, message = "Day number must be at least 1")
  private Integer dayNumber;

  @Schema(example = "TP.HCM - Đà Lạt")
  @NotBlank(message = "Title is required")
  private String title;

  @Schema(example = "Xuất phát lúc 6h sáng, đến Đà Lạt check-in khách sạn")
  @NotBlank(message = "Description is required")
  private String description;
}
