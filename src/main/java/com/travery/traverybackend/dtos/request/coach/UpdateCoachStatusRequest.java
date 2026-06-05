package com.travery.traverybackend.dtos.request.coach;

import com.travery.traverybackend.enums.coach.CoachStatus;
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
public class UpdateCoachStatusRequest {

  @NotNull(message = "Coach status is required")
  private CoachStatus status;
}
