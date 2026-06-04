package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.request.coach.CreateRouteRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateRouteRequest;
import com.travery.traverybackend.dtos.response.coach.RouteResponse;
import java.util.List;
import java.util.UUID;

public interface CoordinatorRouteService {
  RouteResponse createRoute(CreateRouteRequest request);

  List<RouteResponse> getRoutes();

  RouteResponse getRouteDetail(UUID routeId);

  RouteResponse updateRoute(UUID routeId, UpdateRouteRequest request);

  void deleteRoute(UUID routeId);
}
