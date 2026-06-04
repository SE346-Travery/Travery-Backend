package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import java.util.UUID;

public interface GuideCoachTripService {
  CoachTripDetailResponse updateTripStatus(
      UUID guideId, UUID tripId, UpdateCoachTripStatusRequest request);

  void markPassengerNoShow(UUID guideId, UUID tripId, UUID bookingId);
}
