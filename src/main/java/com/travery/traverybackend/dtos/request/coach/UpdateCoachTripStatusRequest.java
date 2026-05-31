package com.travery.traverybackend.dtos.request.coach;

import com.travery.traverybackend.enums.coach.CoachTripStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCoachTripStatusRequest {
  @NotNull(message = "Status is required")
  private CoachTripStatus status;
}
