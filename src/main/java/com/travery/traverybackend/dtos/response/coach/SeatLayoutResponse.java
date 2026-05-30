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
public class SeatLayoutResponse {
  private UUID id;
  private String name;
  private CoachType coachType;
  private int totalSeats;
  private List<SeatLayoutItemResponse> items;
}
