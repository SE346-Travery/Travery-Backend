package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.coach.CoachStatus;
import com.travery.traverybackend.enums.coach.CoachType;
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
public class CoachResponse {
  private UUID id;
  private String licensePlate;
  private CoachType coachType;
  private int capacity;
  private CoachStatus status;
  private String seatLayoutName;
}
