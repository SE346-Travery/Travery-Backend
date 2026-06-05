package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.coach.CoachTripStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachTripDetailResponse {
  private UUID id;
  private LocalDateTime departureTime;
  private LocalDateTime arrivalTime;
  private CoachTripStatus status;

  // Route info
  private UUID routeId;
  private String originDestinationName;
  private String destinationDestinationName;
  private BigDecimal basePrice;

  // Coach info
  private UUID coachId;
  private String coachLicensePlate;
  private String coachType;

  // Driver info
  private UUID driverId;
  private String driverName;
  private String driverPhone;

  // Guide info
  private UUID guideId;
  private String guideName;
  private String guidePhone;

  // Booking statistics
  private int totalSeats;
  private int availableSeats;
  private int bookingsCount;
  private int passengersCount;
}
