package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.CreateStationRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateStationRequest;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.DriverResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.enums.coach.CoachType;
import java.util.List;
import java.util.UUID;

public interface AdminCoachService {

  SeatLayoutResponse createSeatLayout(CreateSeatLayoutRequest request);

  List<SeatLayoutResponse> getSeatLayouts(CoachType coachType);

  SeatLayoutResponse getSeatLayoutDetail(UUID layoutId);

  CoachResponse createCoach(CreateCoachRequest request);

  List<CoachResponse> getCoaches();

  CoachResponse getCoachDetail(UUID coachId);

  CoachResponse updateCoach(UUID coachId, UpdateCoachRequest request);

  void deleteCoach(UUID coachId);

  DriverResponse createDriver(CreateDriverRequest request);

  List<DriverResponse> getDrivers();

  DriverResponse getDriverDetail(UUID driverId);

  DriverResponse updateDriver(UUID driverId, UpdateDriverRequest request);

  void deleteDriver(UUID driverId);

  StationResponse createStation(CreateStationRequest request);

  List<StationResponse> getStations();

  StationResponse getStationDetail(UUID stationId);

  StationResponse updateStation(UUID stationId, UpdateStationRequest request);

  void deleteStation(UUID stationId);
}
