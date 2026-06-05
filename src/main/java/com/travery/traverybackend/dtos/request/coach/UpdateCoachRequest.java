package com.travery.traverybackend.dtos.request.coach;

import com.travery.traverybackend.enums.coach.CoachStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
public class UpdateCoachRequest {

  @Size(max = 20, message = "License plate must not exceed 20 characters")
  private String licensePlate;

  private CoachType coachType;

  @Min(value = 1, message = "Capacity must be at least 1")
  private Integer capacity;

  private CoachStatus status;

  private UUID seatLayoutId;
}
