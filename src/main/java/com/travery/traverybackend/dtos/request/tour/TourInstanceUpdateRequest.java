package com.travery.traverybackend.dtos.request.tour;

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
public class TourInstanceUpdateRequest {
  private UUID coordinatorId;
  private UUID guideId;
  private UUID coachId;
  private UUID driverId;
  private UUID hotelBookingId;
  private LocalDate startDate;
  private LocalDate endDate;
  private TourInstanceStatus status;
}
