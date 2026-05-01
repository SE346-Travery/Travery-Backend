package com.travery.traverybackend.dtos.request.tour;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
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
public class TourItineraryDayRequest {
  @Positive(message = "Day number must be positive")
  private int dayNumber;

  @NotBlank(message = "Title is required")
  private String title;

  private String description;

  @PositiveOrZero(message = "Meal count must be zero or positive")
  private int mealCount;

  private List<String> imageUrls;
}
