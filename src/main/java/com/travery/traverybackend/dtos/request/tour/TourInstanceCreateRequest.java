package com.travery.traverybackend.dtos.request.tour;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class TourInstanceCreateRequest {

  @NotNull(message = "Tour ID is required")
  private UUID tourId;

  @NotNull(message = "Start date is required")
  @Future(message = "Start date must be in the future")
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  @Future(message = "End date must be in the future")
  private LocalDate endDate;

  private Integer maxParticipants;
  private Integer minParticipants;
}
