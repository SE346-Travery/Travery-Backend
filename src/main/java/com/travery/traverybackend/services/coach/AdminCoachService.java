package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachStatusRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateDriverRequest;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.DriverResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
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

  CoachResponse updateCoachStatus(UUID coachId, UpdateCoachStatusRequest request);

  void deleteCoach(UUID coachId);

  List<DriverResponse> getDrivers();

  DriverResponse getDriverDetail(UUID driverId);

  DriverResponse createDriver(CreateDriverRequest request);

  DriverResponse updateDriver(UUID driverId, UpdateDriverRequest request);

  void deleteDriver(UUID driverId);
}
