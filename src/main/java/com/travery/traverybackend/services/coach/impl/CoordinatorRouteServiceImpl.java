package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.CreateRouteRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateRouteRequest;
import com.travery.traverybackend.dtos.response.coach.RouteResponse;
import com.travery.traverybackend.entities.coach.Route;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.enums.finance.RefundServiceType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.CoachErrorCode;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.RouteRepository;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.repositories.finance.RefundPolicyRepository;
import com.travery.traverybackend.services.coach.CoordinatorRouteService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoordinatorRouteServiceImpl implements CoordinatorRouteService {

  private final RouteRepository routeRepository;
  private final DestinationRepository destinationRepository;
  private final RefundPolicyRepository refundPolicyRepository;
  private final CoachMapper coachMapper;

  @Override
  @Transactional
  public RouteResponse createRoute(CreateRouteRequest request) {
    validateDifferentDestinations(
        request.getOriginDestinationId(), request.getDestinationDestinationId());

    Destination origin = getDestinationById(request.getOriginDestinationId());
    Destination destination = getDestinationById(request.getDestinationDestinationId());
    RefundPolicy refundPolicy = getCoachRefundPolicyById(request.getRefundPolicyId());

    Route route =
        Route.builder()
            .originDestination(origin)
            .destinationDestination(destination)
            .distanceKm(request.getDistanceKm())
            .estimatedHours(request.getEstimatedHours())
            .basePrice(request.getBasePrice())
            .refundPolicy(refundPolicy)
            .build();

    Route saved = routeRepository.save(route);
    return coachMapper.toRouteResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<RouteResponse> getRoutes() {
    return coachMapper.toRouteResponseList(routeRepository.findAllByIsDeletedFalse());
  }

  @Override
  @Transactional(readOnly = true)
  public RouteResponse getRouteDetail(UUID routeId) {
    return coachMapper.toRouteResponse(getActiveRouteById(routeId));
  }

  @Override
  @Transactional
  public RouteResponse updateRoute(UUID routeId, UpdateRouteRequest request) {
    Route route = getActiveRouteById(routeId);

    UUID originId =
        request.getOriginDestinationId() != null
            ? request.getOriginDestinationId()
            : route.getOriginDestination().getId();
    UUID destinationId =
        request.getDestinationDestinationId() != null
            ? request.getDestinationDestinationId()
            : route.getDestinationDestination().getId();
    validateDifferentDestinations(originId, destinationId);

    if (request.getOriginDestinationId() != null) {
      route.setOriginDestination(getDestinationById(request.getOriginDestinationId()));
    }
    if (request.getDestinationDestinationId() != null) {
      route.setDestinationDestination(getDestinationById(request.getDestinationDestinationId()));
    }
    if (request.getDistanceKm() != null) {
      route.setDistanceKm(request.getDistanceKm());
    }
    if (request.getEstimatedHours() != null) {
      route.setEstimatedHours(request.getEstimatedHours());
    }
    if (request.getBasePrice() != null) {
      route.setBasePrice(request.getBasePrice());
    }
    if (request.getRefundPolicyId() != null) {
      route.setRefundPolicy(getCoachRefundPolicyById(request.getRefundPolicyId()));
    }

    Route saved = routeRepository.save(route);
    return coachMapper.toRouteResponse(saved);
  }

  @Override
  @Transactional
  public void deleteRoute(UUID routeId) {
    Route route = getActiveRouteById(routeId);
    route.setDeleted(true);
    routeRepository.save(route);
  }

  private Route getActiveRouteById(UUID routeId) {
    return routeRepository
        .findByIdAndIsDeletedFalse(routeId)
        .orElseThrow(() -> new BaseAppException(CoachErrorCode.ROUTE_NOT_FOUND));
  }

  private Destination getDestinationById(UUID destinationId) {
    return destinationRepository
        .findById(destinationId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Destination not found"));
  }

  private RefundPolicy getCoachRefundPolicyById(UUID refundPolicyId) {
    if (refundPolicyId == null) {
      return null;
    }

    RefundPolicy refundPolicy =
        refundPolicyRepository
            .findByIdAndIsDeletedFalse(refundPolicyId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Refund policy not found"));

    if (refundPolicy.getServiceType() != RefundServiceType.COACH) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Refund policy must be for coach service");
    }

    return refundPolicy;
  }

  private void validateDifferentDestinations(UUID originId, UUID destinationId) {
    if (originId.equals(destinationId)) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Origin and destination must be different");
    }
  }
}
