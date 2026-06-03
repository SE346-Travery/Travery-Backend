package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachStatusRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateDriverRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.DriverResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff/admin")
@RequiredArgsConstructor
public class AdminCoachController extends AbstractBaseController {

  private final AdminCoachService adminCoachService;

  // ===== Driver Endpoints =====

  @GetMapping("/drivers")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<DriverResponse>>> getDrivers() {
    return success(adminCoachService.getDrivers(), "Drivers fetched successfully");
  }

  @GetMapping("/drivers/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<DriverResponse>> getDriverDetail(@PathVariable UUID id) {
    return success(adminCoachService.getDriverDetail(id), "Driver fetched successfully");
  }

  @PostMapping("/drivers")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<DriverResponse>> createDriver(
      @Valid @RequestBody CreateDriverRequest request) {
    return created(adminCoachService.createDriver(request), "Driver created successfully");
  }

  @PutMapping("/drivers/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<DriverResponse>> updateDriver(
      @PathVariable UUID id, @Valid @RequestBody UpdateDriverRequest request) {
    return success(adminCoachService.updateDriver(id, request), "Driver updated successfully");
  }

  @DeleteMapping("/drivers/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteDriver(@PathVariable UUID id) {
    adminCoachService.deleteDriver(id);
    return success("Driver deleted successfully");
  }

  // ===== Seat Layout Endpoints =====

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
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<CoachResponse>>> getCoaches() {
    List<CoachResponse> response = adminCoachService.getCoaches();
    return success(response, "Fetched coaches successfully");
  }

  @GetMapping("/coaches/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<CoachResponse>> getCoachDetail(@PathVariable UUID id) {
    CoachResponse response = adminCoachService.getCoachDetail(id);
    return success(response, "Fetched coach detail successfully");
  }

  @PutMapping("/coaches/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<CoachResponse>> updateCoach(
      @PathVariable UUID id, @Valid @RequestBody UpdateCoachRequest request) {
    CoachResponse response = adminCoachService.updateCoach(id, request);
    return success(response, "Coach updated successfully");
  }

  @PatchMapping("/coaches/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<CoachResponse>> updateCoachStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateCoachStatusRequest request) {
    CoachResponse response = adminCoachService.updateCoachStatus(id, request);
    return success(response, "Coach status updated successfully");
  }

  @DeleteMapping("/coaches/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteCoach(@PathVariable UUID id) {
    adminCoachService.deleteCoach(id);
    return success("Coach soft-deleted successfully");
  }
}
