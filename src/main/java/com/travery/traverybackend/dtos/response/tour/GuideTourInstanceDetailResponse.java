package com.travery.traverybackend.dtos.response.tour;

import com.travery.traverybackend.dtos.response.booking.TourBookingResponse;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import java.time.LocalDate;
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
public class GuideTourInstanceDetailResponse {
  private UUID id;
  private String tourName;
  private String destinationName;
  private String pickupLocation;
  private LocalDate startDate;
  private LocalDate endDate;
  private TourInstanceStatus status;

  private UUID coachId;
  private String coachLicensePlate;
  private String coachType;

  private UUID driverId;
  private String driverName;
  private String driverPhone;

  private List<TourBookingResponse> bookings;
}
