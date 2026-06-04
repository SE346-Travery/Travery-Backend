package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.coach.CreateRouteRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateRouteRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.coach.RouteResponse;
import com.travery.traverybackend.services.coach.CoordinatorRouteService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coordinator/routes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COORDINATOR')")
public class CoordinatorRouteController extends AbstractBaseController {

  private final CoordinatorRouteService routeService;

  @PostMapping
  public ResponseEntity<SingleResponse<RouteResponse>> createRoute(
      @Valid @RequestBody CreateRouteRequest request) {
    RouteResponse response = routeService.createRoute(request);
    return created(response, "Route created successfully");
  }

  @GetMapping
  public ResponseEntity<SingleResponse<List<RouteResponse>>> getRoutes() {
    List<RouteResponse> response = routeService.getRoutes();
    return success(response, "Fetched routes successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<SingleResponse<RouteResponse>> getRouteDetail(@PathVariable UUID id) {
    RouteResponse response = routeService.getRouteDetail(id);
    return success(response, "Fetched route detail successfully");
  }

  @PatchMapping("/{id}")
  public ResponseEntity<SingleResponse<RouteResponse>> updateRoute(
      @PathVariable UUID id, @Valid @RequestBody UpdateRouteRequest request) {
    RouteResponse response = routeService.updateRoute(id, request);
    return success(response, "Route updated successfully");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<SuccessResponse> deleteRoute(@PathVariable UUID id) {
    routeService.deleteRoute(id);
    return success("Route deleted successfully");
  }
}
