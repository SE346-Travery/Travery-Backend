package com.travery.traverybackend.services.coach.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.dtos.request.coach.CreateCoachTripRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.entities.coach.*;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.*;
import com.travery.traverybackend.repositories.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CoordinatorCoachTripServiceImplTest {

  @Mock private CoachTripRepository coachTripRepository;
  @Mock private RouteRepository routeRepository;
  @Mock private CoachRepository coachRepository;
  @Mock private DriverRepository driverRepository;
  @Mock private UserRepository userRepository;
  @Mock private CoachBookingRepository coachBookingRepository;
  @Mock private CoachBookingSeatRepository coachBookingSeatRepository;
  @Mock private CoachMapper coachMapper;

  @InjectMocks private CoordinatorCoachTripServiceImpl coordinatorService;

  private UUID tripId;
  private UUID coordinatorId;
  private UUID routeId;
  private UUID coachId;
  private UUID driverId;
  private UUID guideId;

  private Coordinator coordinator;
  private Route route;
  private Coach coach;
  private Driver driver;
  private Guide guide;
  private CoachTrip trip;

  @BeforeEach
  void setUp() {
    tripId = UUID.randomUUID();
    coordinatorId = UUID.randomUUID();
    routeId = UUID.randomUUID();
    coachId = UUID.randomUUID();
    driverId = UUID.randomUUID();
    guideId = UUID.randomUUID();

    coordinator = Coordinator.builder().id(coordinatorId).build();
    guide = Guide.builder().id(guideId).fullName("Guide Name").build();

    Destination origin = Destination.builder().name("HN").build();
    Destination dest = Destination.builder().name("HCM").build();
    route =
        Route.builder().id(routeId).originDestination(origin).destinationDestination(dest).build();

    SeatLayout seatLayout = SeatLayout.builder().totalSeats(40).items(List.of()).build();
    coach = Coach.builder().id(coachId).coachType(CoachType.BED).seatLayout(seatLayout).build();

    driver = Driver.builder().id(driverId).fullName("John Doe").build();

    trip =
        CoachTrip.builder()
            .id(tripId)
            .route(route)
            .coach(coach)
            .driver(driver)
            .guide(guide)
            .coordinator(coordinator)
            .departureTime(LocalDateTime.now().plusDays(1))
            .status(CoachTripStatus.OPEN)
            .build();
  }

  @Test
  void createTrip_Success() {
    CreateCoachTripRequest request =
        CreateCoachTripRequest.builder()
            .routeId(routeId)
            .coachId(coachId)
            .driverId(driverId)
            .guideId(guideId)
            .departureTime(LocalDateTime.now().plusDays(1))
            .build();

    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(routeRepository.findByIdAndIsDeletedFalse(routeId)).thenReturn(Optional.of(route));
    when(coachRepository.findByIdAndIsDeletedFalse(coachId)).thenReturn(Optional.of(coach));
    when(driverRepository.findByIdAndIsDeletedFalse(driverId)).thenReturn(Optional.of(driver));
    when(userRepository.findActiveGuideById(guideId)).thenReturn(Optional.of(guide));
    when(coachTripRepository.save(any(CoachTrip.class))).thenReturn(trip);
    // removed unused stubs
    when(coachMapper.toCoachTripDetailResponse(any())).thenReturn(new CoachTripDetailResponse());

    CoachTripDetailResponse response = coordinatorService.createTrip(request, coordinatorId);

    assertNotNull(response);
    verify(coachTripRepository).save(any(CoachTrip.class));
  }

  @Test
  void createTrip_WithDeletedRoute_ThrowsException() {
    CreateCoachTripRequest request =
        CreateCoachTripRequest.builder()
            .routeId(routeId)
            .coachId(coachId)
            .driverId(driverId)
            .guideId(guideId)
            .departureTime(LocalDateTime.now().plusDays(1))
            .build();

    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(routeRepository.findByIdAndIsDeletedFalse(routeId)).thenReturn(Optional.empty());

    assertThrows(BaseAppException.class, () -> coordinatorService.createTrip(request, coordinatorId));
    verify(coachTripRepository, never()).save(any(CoachTrip.class));
  }

  @Test
  void createTrip_WithDeletedDriver_ThrowsException() {
    CreateCoachTripRequest request =
        CreateCoachTripRequest.builder()
            .routeId(routeId)
            .coachId(coachId)
            .driverId(driverId)
            .guideId(guideId)
            .departureTime(LocalDateTime.now().plusDays(1))
            .build();

    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(routeRepository.findByIdAndIsDeletedFalse(routeId)).thenReturn(Optional.of(route));
    when(coachRepository.findByIdAndIsDeletedFalse(coachId)).thenReturn(Optional.of(coach));
    when(driverRepository.findByIdAndIsDeletedFalse(driverId)).thenReturn(Optional.empty());

    assertThrows(BaseAppException.class, () -> coordinatorService.createTrip(request, coordinatorId));
    verify(coachTripRepository, never()).save(any(CoachTrip.class));
  }

  @Test
  void getTrips_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<CoachTrip> tripPage = new PageImpl<>(List.of(trip));

    when(coachTripRepository.findAll(pageable)).thenReturn(tripPage);
    when(coachBookingSeatRepository.countBookedSeatsForTrips(any(), any()))
        .thenReturn(java.util.Collections.emptyList());
    when(coachMapper.toCoachTripResponse(eq(trip), anyInt())).thenReturn(new CoachTripResponse());

    Page<CoachTripResponse> response = coordinatorService.getTrips(null, pageable);

    assertNotNull(response);
    assertEquals(1, response.getContent().size());
    verify(coachTripRepository).findAll(pageable);
  }

  @Test
  void getTrips_WithStatus_FiltersByStatusAcrossAllCoordinators() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<CoachTrip> tripPage = new PageImpl<>(List.of(trip));

    when(coachTripRepository.findByStatus(CoachTripStatus.OPEN, pageable)).thenReturn(tripPage);
    when(coachBookingSeatRepository.countBookedSeatsForTrips(any(), any()))
        .thenReturn(java.util.Collections.emptyList());
    when(coachMapper.toCoachTripResponse(eq(trip), anyInt())).thenReturn(new CoachTripResponse());

    Page<CoachTripResponse> response = coordinatorService.getTrips(CoachTripStatus.OPEN, pageable);

    assertNotNull(response);
    assertEquals(1, response.getContent().size());
    verify(coachTripRepository).findByStatus(CoachTripStatus.OPEN, pageable);
  }

  @Test
  void getTripDetail_Success() {
    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    when(coachMapper.toCoachTripDetailResponse(any()))
        .thenReturn(CoachTripDetailResponse.builder().bookingsCount(5).build());

    CoachTripDetailResponse response = coordinatorService.getTripDetail(tripId);

    assertNotNull(response);
    assertEquals(5, response.getBookingsCount());
  }

  @Test
  void reassignCoach_Success() {
    UUID newCoachId = UUID.randomUUID();
    Coach newCoach =
        Coach.builder()
            .id(newCoachId)
            .coachType(CoachType.LIMOUSINE)
            .seatLayout(coach.getSeatLayout())
            .build();

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(coachRepository.findByIdAndIsDeletedFalse(newCoachId)).thenReturn(Optional.of(newCoach));
    when(coachTripRepository.save(any(CoachTrip.class))).thenReturn(trip);
    when(coachMapper.toCoachTripDetailResponse(any())).thenReturn(new CoachTripDetailResponse());

    CoachTripDetailResponse response = coordinatorService.reassignCoach(tripId, newCoachId);

    assertNotNull(response);
    verify(coachTripRepository).save(trip);
  }

  @Test
  void reassignCoach_WithDeletedCoach_ThrowsException() {
    UUID newCoachId = UUID.randomUUID();

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(coachRepository.findByIdAndIsDeletedFalse(newCoachId)).thenReturn(Optional.empty());

    assertThrows(BaseAppException.class, () -> coordinatorService.reassignCoach(tripId, newCoachId));
    verify(coachTripRepository, never()).save(any(CoachTrip.class));
  }

  @Test
  void reassignDriver_Success() {
    UUID newDriverId = UUID.randomUUID();
    Driver newDriver = Driver.builder().id(newDriverId).fullName("Jane Doe").build();

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(driverRepository.findByIdAndIsDeletedFalse(newDriverId)).thenReturn(Optional.of(newDriver));
    when(coachTripRepository.save(any(CoachTrip.class))).thenReturn(trip);
    when(coachMapper.toCoachTripDetailResponse(any())).thenReturn(new CoachTripDetailResponse());

    CoachTripDetailResponse response = coordinatorService.reassignDriver(tripId, newDriverId);

    assertNotNull(response);
    verify(coachTripRepository).save(trip);
  }

  @Test
  void reassignDriver_WithDeletedDriver_ThrowsException() {
    UUID newDriverId = UUID.randomUUID();

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(driverRepository.findByIdAndIsDeletedFalse(newDriverId)).thenReturn(Optional.empty());

    assertThrows(BaseAppException.class, () -> coordinatorService.reassignDriver(tripId, newDriverId));
    verify(coachTripRepository, never()).save(any(CoachTrip.class));
  }

  @Test
  void getTripDetail_NotFound_ThrowsException() {
    when(coachTripRepository.findById(tripId)).thenReturn(Optional.empty());

    assertThrows(BaseAppException.class, () -> coordinatorService.getTripDetail(tripId));
  }
}
