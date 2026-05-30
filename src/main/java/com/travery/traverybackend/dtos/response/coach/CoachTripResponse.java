package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class CoachTripResponse {
  private UUID id;
  private LocalDateTime departureTime;
  private LocalDateTime arrivalTime;
  private CoachType coachType;
  private int totalSeats;
  private int availableSeats;
  private BigDecimal basePrice;
  private DestinationWithStationsResponse originDestination;
  private DestinationWithStationsResponse destinationDestination;
  private CoachTripStatus status;
}
