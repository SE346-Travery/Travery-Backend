package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.request.coach.CreateCoachTripRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoordinatorCoachTripService {
  CoachTripDetailResponse createTrip(CreateCoachTripRequest request, UUID coordinatorId);

  Page<CoachTripResponse> getTrips(CoachTripStatus status, Pageable pageable);

  CoachTripDetailResponse getTripDetail(UUID tripId);

  CoachTripDetailResponse reassignCoach(UUID tripId, UUID newCoachId);

  CoachTripDetailResponse reassignDriver(UUID tripId, UUID newDriverId);
}
