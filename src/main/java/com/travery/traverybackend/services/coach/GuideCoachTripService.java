package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.request.coach.GuideCoachAttendanceRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import java.util.List;
import java.util.UUID;

public interface GuideCoachTripService {
  List<CoachTripResponse> getAssignedTrips(UUID guideId, String filter);

  CoachTripDetailResponse getTripDetail(UUID guideId, UUID tripId);

  CoachTripDetailResponse recordAttendance(
      UUID guideId, UUID tripId, GuideCoachAttendanceRequest request);

  CoachTripDetailResponse updateTripStatus(UUID guideId, UUID tripId, UpdateCoachTripStatusRequest request);
  }

