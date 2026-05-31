package com.travery.traverybackend.dtos.request.coach;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCoachTripRequest {
  @NotNull(message = "Route ID is required")
  private UUID routeId;

  @NotNull(message = "Coach ID is required")
  private UUID coachId;

  @NotNull(message = "Driver ID is required")
  private UUID driverId;

  @NotNull(message = "Departure time is required")
  @Future(message = "Departure time must be in the future")
  private LocalDateTime departureTime;
}
