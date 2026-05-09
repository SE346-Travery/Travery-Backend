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
public class TourInstanceDetailResponse {
  private UUID id;
  private String tourName;
  private String destinationName;
  private String pickupLocation;
  private LocalDate startDate;
  private LocalDate endDate;
  private int minParticipants;
  private int maxParticipants;
  private int currentParticipants;
  private TourInstanceStatus status;

  private UUID guideId;
  private String guideName;
  private String guidePhone;

  private UUID coachId;
  private String coachLicensePlate;
  private String coachType;

  private UUID driverId;
  private String driverName;
  private String driverPhone;
}
