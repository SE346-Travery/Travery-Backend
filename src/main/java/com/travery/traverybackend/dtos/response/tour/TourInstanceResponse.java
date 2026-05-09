package com.travery.traverybackend.dtos.response.tour;

import com.travery.traverybackend.enums.tour.TourInstanceStatus;
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
public class TourInstanceResponse {
  private UUID id;
  private String tourName;
  private LocalDate startDate;
  private LocalDate endDate;
  private int minParticipants;
  private int maxParticipants;
  private int currentParticipants;
  private TourInstanceStatus status;
}
