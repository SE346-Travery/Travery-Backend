package com.travery.traverybackend.dtos.request.coach;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReassignCoachRequest {
  @NotNull(message = "Coach ID is required")
  private UUID coachId;
}
