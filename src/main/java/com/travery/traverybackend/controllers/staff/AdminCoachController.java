package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.CreateStationRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateStationRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.DriverResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.services.coach.AdminCoachService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCoachController extends AbstractBaseController {

  private final AdminCoachService adminCoachService;

  @PostMapping("/seat-layouts")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<SeatLayoutResponse>> createSeatLayout(
      @Valid @RequestBody CreateSeatLayoutRequest request) {
    SeatLayoutResponse response = adminCoachService.createSeatLayout(request);
    return created(response, "Seat layout created successfully");
  }

  @GetMapping("/seat-layouts")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<SeatLayoutResponse>>> getSeatLayouts(
      @RequestParam(required = false) CoachType coachType) {
    List<SeatLayoutResponse> response = adminCoachService.getSeatLayouts(coachType);
    return success(response, "Fetched seat layouts successfully");
  }

  @GetMapping("/seat-layouts/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<SeatLayoutResponse>> getSeatLayoutDetail(
      @PathVariable UUID id) {
    SeatLayoutResponse response = adminCoachService.getSeatLayoutDetail(id);
    return success(response, "Fetched seat layout detail successfully");
  }

  @PostMapping("/coaches")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<CoachResponse>> createCoach(
      @Valid @RequestBody CreateCoachRequest request) {
    CoachResponse response = adminCoachService.createCoach(request);
    return created(response, "Coach created successfully");
  }

  @GetMapping("/coaches")
  @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
  public ResponseEntity<SingleResponse<List<CoachResponse>>> getCoaches() {
    List<CoachResponse> response = adminCoachService.getCoaches();
    return success(response, "Fetched coaches successfully");
  }

  @GetMapping("/coaches/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
  public ResponseEntity<SingleResponse<CoachResponse>> getCoachDetail(@PathVariable UUID id) {
    CoachResponse response = adminCoachService.getCoachDetail(id);
    return success(response, "Fetched coach detail successfully");
  }

  @PatchMapping("/coaches/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<CoachResponse>> updateCoach(
      @PathVariable UUID id, @Valid @RequestBody UpdateCoachRequest request) {
    CoachResponse response = adminCoachService.updateCoach(id, request);
    return success(response, "Coach updated successfully");
  }

  @DeleteMapping("/coaches/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteCoach(@PathVariable UUID id) {
    adminCoachService.deleteCoach(id);
    return success("Coach deleted successfully");
  }

  @PostMapping("/drivers")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<DriverResponse>> createDriver(
      @Valid @RequestBody CreateDriverRequest request) {
    DriverResponse response = adminCoachService.createDriver(request);
    return created(response, "Driver created successfully");
  }

  @GetMapping("/drivers")
  @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
  public ResponseEntity<SingleResponse<List<DriverResponse>>> getDrivers() {
    List<DriverResponse> response = adminCoachService.getDrivers();
    return success(response, "Fetched drivers successfully");
  }

  @GetMapping("/drivers/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
  public ResponseEntity<SingleResponse<DriverResponse>> getDriverDetail(@PathVariable UUID id) {
    DriverResponse response = adminCoachService.getDriverDetail(id);
    return success(response, "Fetched driver detail successfully");
  }

  @PatchMapping("/drivers/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<DriverResponse>> updateDriver(
      @PathVariable UUID id, @Valid @RequestBody UpdateDriverRequest request) {
    DriverResponse response = adminCoachService.updateDriver(id, request);
    return success(response, "Driver updated successfully");
  }

  @DeleteMapping("/drivers/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteDriver(@PathVariable UUID id) {
    adminCoachService.deleteDriver(id);
    return success("Driver deleted successfully");
  }

  @PostMapping("/stations")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<StationResponse>> createStation(
      @Valid @RequestBody CreateStationRequest request) {
    StationResponse response = adminCoachService.createStation(request);
    return created(response, "Station created successfully");
  }

  @GetMapping("/stations")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<StationResponse>>> getStations() {
    List<StationResponse> response = adminCoachService.getStations();
    return success(response, "Fetched stations successfully");
  }

  @GetMapping("/stations/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<StationResponse>> getStationDetail(@PathVariable UUID id) {
    StationResponse response = adminCoachService.getStationDetail(id);
    return success(response, "Fetched station detail successfully");
  }

  @PatchMapping("/stations/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<StationResponse>> updateStation(
      @PathVariable UUID id, @Valid @RequestBody UpdateStationRequest request) {
    StationResponse response = adminCoachService.updateStation(id, request);
    return success(response, "Station updated successfully");
  }

  @DeleteMapping("/stations/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteStation(@PathVariable UUID id) {
    adminCoachService.deleteStation(id);
    return success("Station deleted successfully");
  }
}
