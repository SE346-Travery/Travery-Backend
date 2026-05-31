package com.travery.traverybackend.dtos.request.coach;

import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.enums.coach.DepartureTimeSlot;
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
public class SearchCoachTripRequest {

  @NotNull(message = "Origin ID is required (Province)")
  private UUID originId;

  @NotNull(message = "Destination ID is required (Province)")
  private UUID destinationId;

  @NotNull(message = "Departure date is required")
  private LocalDate departureDate;

  private CoachType coachType;

  private DepartureTimeSlot departureTimeSlot;

  private Boolean sortByPriceAsc;
}
