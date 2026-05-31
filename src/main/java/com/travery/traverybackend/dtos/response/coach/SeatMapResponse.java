package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.coach.CoachType;
import java.util.List;
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
public class SeatMapResponse {
  private UUID tripId;
  private CoachType coachType;
  private int totalSeats;
  private int availableSeats;
  private List<SeatStatusResponse> seats;
}
