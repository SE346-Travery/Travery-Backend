package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.dtos.response.coach.GuideBookingResponse;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuideCoachTripService {

  /** Retrieves the list of trips assigned to the guide. */
  Page<CoachTripResponse> getMyTrips(UUID guideId, CoachTripStatus status, Pageable pageable);

  /** Retrieves details of a specific trip (validates assigned guide). */
  CoachTripDetailResponse getTripDetail(UUID guideId, UUID tripId);

  /** Retrieves the list of bookings for attendance check-in. */
  List<GuideBookingResponse> getTripAttendance(UUID guideId, UUID tripId);

  /** Updates the trip status with state machine validation. */
  CoachTripDetailResponse updateTripStatus(
      UUID guideId, UUID tripId, UpdateCoachTripStatusRequest request);

  /** Confirms passenger presence (PAID -> CHECKED_IN). */
  void checkInBooking(UUID guideId, UUID tripId, UUID bookingId);

  /** Marks passenger as absent (PAID -> NO_SHOW). */
  void markPassengerNoShow(UUID guideId, UUID tripId, UUID bookingId);
}
