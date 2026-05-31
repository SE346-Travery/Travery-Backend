package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.request.coach.SearchCoachTripRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.dtos.response.coach.SeatMapResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import java.util.List;
import java.util.UUID;

public interface CoachTripService {

  List<StationResponse> getStations();

  List<CoachTripResponse> searchTrips(SearchCoachTripRequest request);

  SeatMapResponse getSeatMap(UUID tripId);
}
