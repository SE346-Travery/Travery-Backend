package com.travery.traverybackend.services.coach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.request.coach.CreateRouteRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateRouteRequest;
import com.travery.traverybackend.dtos.response.coach.RouteResponse;
import com.travery.traverybackend.entities.coach.Route;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.enums.finance.RefundServiceType;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.RouteRepository;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.repositories.finance.RefundPolicyRepository;
import com.travery.traverybackend.services.coach.impl.CoordinatorRouteServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoordinatorRouteServiceTest {

  @Mock private RouteRepository routeRepository;
  @Mock private DestinationRepository destinationRepository;
  @Mock private RefundPolicyRepository refundPolicyRepository;
  @Mock private CoachMapper coachMapper;

  @InjectMocks private CoordinatorRouteServiceImpl routeService;

  private UUID routeId;
  private UUID originId;
  private UUID destinationId;
  private UUID refundPolicyId;
  private Destination origin;
  private Destination destination;
  private RefundPolicy refundPolicy;
  private Route route;
  private RouteResponse routeResponse;

  @BeforeEach
  void setUp() {
    routeId = UUID.randomUUID();
    originId = UUID.randomUUID();
    destinationId = UUID.randomUUID();
    refundPolicyId = UUID.randomUUID();

    origin = Destination.builder().name("Ho Chi Minh").code("HCM").build();
    origin.setId(originId);
    destination = Destination.builder().name("Da Lat").code("DL").build();
    destination.setId(destinationId);
    refundPolicy =
        RefundPolicy.builder()
            .id(refundPolicyId)
            .name("Coach Standard")
            .serviceType(RefundServiceType.COACH)
            .build();

    route =
        Route.builder()
            .id(routeId)
            .originDestination(origin)
            .destinationDestination(destination)
            .distanceKm(new BigDecimal("300.50"))
            .estimatedHours(new BigDecimal("6.5"))
            .basePrice(new BigDecimal("250000.00"))
            .refundPolicy(refundPolicy)
            .build();

    routeResponse =
        RouteResponse.builder()
            .id(routeId)
            .originDestinationId(originId)
            .originDestinationName("Ho Chi Minh")
            .destinationDestinationId(destinationId)
            .destinationDestinationName("Da Lat")
            .distanceKm(new BigDecimal("300.50"))
            .estimatedHours(new BigDecimal("6.5"))
            .basePrice(new BigDecimal("250000.00"))
            .refundPolicyId(refundPolicyId)
            .refundPolicyName("Coach Standard")
            .build();
  }

  @Test
  void createRoute_validRequest_returnsResponse() {
    CreateRouteRequest request =
        CreateRouteRequest.builder()
            .originDestinationId(originId)
            .destinationDestinationId(destinationId)
            .distanceKm(new BigDecimal("300.50"))
            .estimatedHours(new BigDecimal("6.5"))
            .basePrice(new BigDecimal("250000.00"))
            .refundPolicyId(refundPolicyId)
            .build();

    when(destinationRepository.findById(originId)).thenReturn(Optional.of(origin));
    when(destinationRepository.findById(destinationId)).thenReturn(Optional.of(destination));
    when(refundPolicyRepository.findByIdAndIsDeletedFalse(refundPolicyId))
        .thenReturn(Optional.of(refundPolicy));
    when(routeRepository.save(any(Route.class))).thenReturn(route);
    when(coachMapper.toRouteResponse(route)).thenReturn(routeResponse);

    RouteResponse result = routeService.createRoute(request);

    assertEquals(routeId, result.getId());
    verify(routeRepository).save(any(Route.class));
  }

  @Test
  void getRoutes_returnsNonDeletedRoutes() {
    when(routeRepository.findAllByIsDeletedFalse()).thenReturn(List.of(route));
    when(coachMapper.toRouteResponseList(List.of(route))).thenReturn(List.of(routeResponse));

    List<RouteResponse> result = routeService.getRoutes();

    assertEquals(1, result.size());
    verify(routeRepository).findAllByIsDeletedFalse();
  }

  @Test
  void updateRoute_withPartialRequest_updatesOnlyProvidedFields() {
    UpdateRouteRequest request =
        UpdateRouteRequest.builder()
            .basePrice(new BigDecimal("300000.00"))
            .refundPolicyId(refundPolicyId)
            .build();

    when(routeRepository.findByIdAndIsDeletedFalse(routeId)).thenReturn(Optional.of(route));
    when(refundPolicyRepository.findByIdAndIsDeletedFalse(refundPolicyId))
        .thenReturn(Optional.of(refundPolicy));
    when(routeRepository.save(route)).thenReturn(route);
    when(coachMapper.toRouteResponse(route)).thenReturn(routeResponse);

    routeService.updateRoute(routeId, request);

    assertEquals(new BigDecimal("300000.00"), route.getBasePrice());
    assertEquals(new BigDecimal("300.50"), route.getDistanceKm());
    assertEquals(refundPolicy, route.getRefundPolicy());
    verify(routeRepository).save(route);
  }

  @Test
  void deleteRoute_setsDeletedTrue() {
    when(routeRepository.findByIdAndIsDeletedFalse(routeId)).thenReturn(Optional.of(route));

    routeService.deleteRoute(routeId);

    assertEquals(true, route.isDeleted());
    verify(routeRepository).save(route);
  }
}
