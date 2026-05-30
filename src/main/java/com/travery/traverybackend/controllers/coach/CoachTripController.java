package com.travery.traverybackend.controllers.coach;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.coach.SearchCoachTripRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.dtos.response.coach.SeatMapResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.services.coach.CoachTripService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CoachTripController extends AbstractBaseController {

  private final CoachTripService coachTripService;

  @GetMapping("/stations")
  public ResponseEntity<SingleResponse<List<StationResponse>>> getStations() {
    List<StationResponse> response = coachTripService.getStations();
    return success(response, "Fetched stations successfully");
  }

  @PostMapping("/coach-trips/search")
  public ResponseEntity<SingleResponse<List<CoachTripResponse>>> searchTrips(
      @Valid @RequestBody SearchCoachTripRequest request) {
    List<CoachTripResponse> response = coachTripService.searchTrips(request);
    return success(response, "Searched trips successfully");
  }

  @GetMapping("/coach-trips/{tripId}/seats")
  public ResponseEntity<SingleResponse<SeatMapResponse>> getSeatMap(@PathVariable UUID tripId) {
    SeatMapResponse response = coachTripService.getSeatMap(tripId);
    return success(response, "Fetched seat map successfully");
  }
}
