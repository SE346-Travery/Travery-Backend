package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.CreateCoachTripRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.entities.coach.Driver;
import com.travery.traverybackend.entities.coach.Route;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.CoachErrorCode;
import com.travery.traverybackend.exception.error.UserErrorCode;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachBookingSeatRepository;
import com.travery.traverybackend.repositories.coach.CoachRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.repositories.coach.DriverRepository;
import com.travery.traverybackend.repositories.coach.RouteRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.coach.CoordinatorCoachTripService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoordinatorCoachTripServiceImpl implements CoordinatorCoachTripService {

  private final CoachTripRepository coachTripRepository;
  private final RouteRepository routeRepository;
  private final CoachRepository coachRepository;
  private final DriverRepository driverRepository;
  private final UserRepository userRepository;
  private final CoachBookingSeatRepository coachBookingSeatRepository;
  private final CoachMapper coachMapper;

  @Override
  @Transactional
  public CoachTripDetailResponse createTrip(CreateCoachTripRequest request, UUID coordinatorId) {
    Coordinator coordinator =
        (Coordinator)
            userRepository
                .findById(coordinatorId)
                .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    Route route =
        routeRepository
            .findById(request.getRouteId())
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.ROUTE_NOT_FOUND));

    Coach coach =
        coachRepository
            .findById(request.getCoachId())
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_NOT_FOUND));

    Driver driver =
        driverRepository
            .findById(request.getDriverId())
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.DRIVER_NOT_FOUND));

    CoachTrip trip =
        CoachTrip.builder()
            .route(route)
            .coach(coach)
            .driver(driver)
            .coordinator(coordinator)
            .departureTime(request.getDepartureTime())
            .status(CoachTripStatus.OPEN)
            .build();

    trip = coachTripRepository.save(trip);
    return coachMapper.toCoachTripDetailResponse(trip);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CoachTripResponse> getTrips(
      UUID coordinatorId, CoachTripStatus status, Pageable pageable) {
    Page<CoachTrip> tripPage;
    if (status != null) {
      tripPage = coachTripRepository.findByCoordinator_IdAndStatus(coordinatorId, status, pageable);
    } else {
      tripPage = coachTripRepository.findByCoordinator_Id(coordinatorId, pageable);
    }

    List<UUID> tripIds =
        tripPage.getContent().stream().map(CoachTrip::getId).collect(Collectors.toList());

    Map<UUID, Long> bookedSeatsMap = new HashMap<>();
    if (!tripIds.isEmpty()) {
      List<Object[]> results =
          coachBookingSeatRepository.countBookedSeatsForTrips(
              tripIds, List.of(BookingStatus.CANCELLED, BookingStatus.NO_SHOW));
      for (Object[] result : results) {
        bookedSeatsMap.put((UUID) result[0], (Long) result[1]);
      }
    }

    return tripPage.map(
        trip -> {
          int totalSeats =
              trip.getCoach().getSeatLayout() != null
                  ? trip.getCoach().getSeatLayout().getTotalSeats()
                  : 0;
          long bookedSeats = bookedSeatsMap.getOrDefault(trip.getId(), 0L);
          int availableSeats = totalSeats - (int) bookedSeats;
          return coachMapper.toCoachTripResponse(trip, availableSeats);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public CoachTripDetailResponse getTripDetail(UUID tripId) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));
    return coachMapper.toCoachTripDetailResponse(trip);
  }

  @Override
  @Transactional
  public CoachTripDetailResponse reassignCoach(UUID tripId, UUID newCoachId) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));

    Coach newCoach =
        coachRepository
            .findById(newCoachId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_NOT_FOUND));

    trip.setCoach(newCoach);
    trip = coachTripRepository.save(trip);
    return coachMapper.toCoachTripDetailResponse(trip);
  }

  @Override
  @Transactional
  public CoachTripDetailResponse reassignDriver(UUID tripId, UUID newDriverId) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));

    Driver newDriver =
        driverRepository
            .findById(newDriverId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.DRIVER_NOT_FOUND));

    trip.setDriver(newDriver);
    trip = coachTripRepository.save(trip);
    return coachMapper.toCoachTripDetailResponse(trip);
  }
}
