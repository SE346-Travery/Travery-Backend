package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.SearchCoachTripRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse;
import com.travery.traverybackend.dtos.response.coach.SeatMapResponse;
import com.travery.traverybackend.dtos.response.coach.SeatStatusResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.entities.booking.CoachBookingSeat;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.coach.DepartureTimeSlot;
import com.travery.traverybackend.enums.coach.SeatStatus;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachBookingSeatRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.repositories.coach.StationRepository;
import com.travery.traverybackend.services.coach.CoachTripService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoachTripServiceImpl implements CoachTripService {

  private final StationRepository stationRepository;
  private final CoachTripRepository coachTripRepository;
  private final CoachBookingSeatRepository coachBookingSeatRepository;
  private final CoachMapper coachMapper;

  @Override
  @Transactional(readOnly = true)
  public List<StationResponse> getStations() {
    return coachMapper.toStationResponseList(stationRepository.findAllByIsDeletedFalse());
  }

  @Override
  @Transactional(readOnly = true)
  public List<CoachTripResponse> searchTrips(SearchCoachTripRequest request) {
    LocalDateTime startOfDay = request.getDepartureDate().atStartOfDay();
    LocalDateTime endOfDay = request.getDepartureDate().atTime(LocalTime.MAX);

    List<CoachTrip> trips =
        coachTripRepository.searchTrips(
            request.getOriginId(), request.getDestinationId(), startOfDay, endOfDay);

    trips =
        trips.stream()
            .filter(t -> t.getCoach() != null && !t.getCoach().isDeleted())
            .collect(Collectors.toList());

    // Apply filters
    if (request.getCoachType() != null) {
      trips =
          trips.stream()
              .filter(t -> t.getCoach().getCoachType() == request.getCoachType())
              .collect(Collectors.toList());
    }

    if (request.getDepartureTimeSlot() != null) {
      trips =
          trips.stream()
              .filter(t -> matchesTimeSlot(t.getDepartureTime(), request.getDepartureTimeSlot()))
              .collect(Collectors.toList());
    }

    if (trips.isEmpty()) {
      return new ArrayList<>();
    }

    List<UUID> tripIds = trips.stream().map(CoachTrip::getId).collect(Collectors.toList());
    List<BookingStatus> excludedStatuses = List.of(BookingStatus.CANCELLED, BookingStatus.NO_SHOW);
    List<Object[]> bookedCounts =
        coachBookingSeatRepository.countBookedSeatsForTrips(tripIds, excludedStatuses);

    Map<UUID, Integer> bookedSeatsMap = new HashMap<>();
    for (Object[] count : bookedCounts) {
      bookedSeatsMap.put((UUID) count[0], ((Number) count[1]).intValue());
    }

    Map<UUID, List<StationResponse>> stationsByDestinationId =
        getActiveStationsByDestinationId(trips);

    List<CoachTripResponse> responses = new ArrayList<>();

    for (CoachTrip trip : trips) {
      int totalSeats = trip.getCoach().getSeatLayout().getTotalSeats();

      // Count booked seats from map
      int bookedSeats = bookedSeatsMap.getOrDefault(trip.getId(), 0);
      int availableSeats = totalSeats - bookedSeats;

      CoachTripResponse response =
          CoachTripResponse.builder()
              .id(trip.getId())
              .departureTime(trip.getDepartureTime())
              .arrivalTime(trip.getArrivalTime())
              .coachType(trip.getCoach().getCoachType())
              .totalSeats(totalSeats)
              .availableSeats(availableSeats)
              .basePrice(trip.getRoute().getBasePrice())
              .originDestination(
                  toDestinationWithStationsResponse(
                      trip.getRoute().getOriginDestination(), stationsByDestinationId))
              .destinationDestination(
                  toDestinationWithStationsResponse(
                      trip.getRoute().getDestinationDestination(), stationsByDestinationId))
              .status(trip.getStatus())
              .build();
      responses.add(response);
    }

    // Apply sorting
    if (request.getSortByPriceAsc() != null) {
      if (request.getSortByPriceAsc()) {
        responses.sort(Comparator.comparing(CoachTripResponse::getBasePrice));
      } else {
        responses.sort(Comparator.comparing(CoachTripResponse::getBasePrice).reversed());
      }
    } else {
      // Default sort by departure time
      responses.sort(Comparator.comparing(CoachTripResponse::getDepartureTime));
    }

    return responses;
  }

  @Override
  @Transactional(readOnly = true)
  public SeatMapResponse getSeatMap(UUID tripId) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Coach trip not found"));

    List<BookingStatus> excludedStatuses = List.of(BookingStatus.CANCELLED, BookingStatus.NO_SHOW);
    List<CoachBookingSeat> bookedSeats =
        coachBookingSeatRepository.findByTripIdAndBookingStatusNotIn(tripId, excludedStatuses);

    Set<UUID> bookedSeatItemIds =
        bookedSeats.stream().map(bs -> bs.getSeatLayoutItem().getId()).collect(Collectors.toSet());

    List<SeatLayoutItem> items = trip.getCoach().getSeatLayout().getItems();
    List<SeatStatusResponse> seatStatuses = new ArrayList<>();

    for (SeatLayoutItem item : items) {
      SeatStatus status =
          bookedSeatItemIds.contains(item.getId()) ? SeatStatus.BOOKED : SeatStatus.AVAILABLE;

      seatStatuses.add(
          SeatStatusResponse.builder()
              .seatLayoutItemId(item.getId())
              .seatName(item.getSeatName())
              .tier(item.getTier())
              .position(item.getPosition())
              .rowNumber(item.getRowNumber())
              .columnNumber(item.getColumnNumber())
              .status(status)
              .build());
    }

    // Sort by row then column
    seatStatuses.sort(
        Comparator.comparing(SeatStatusResponse::getRowNumber)
            .thenComparing(SeatStatusResponse::getColumnNumber));

    return SeatMapResponse.builder()
        .tripId(tripId)
        .coachType(trip.getCoach().getCoachType())
        .totalSeats(trip.getCoach().getSeatLayout().getTotalSeats())
        .availableSeats(trip.getCoach().getSeatLayout().getTotalSeats() - bookedSeatItemIds.size())
        .seats(seatStatuses)
        .build();
  }

  private boolean matchesTimeSlot(LocalDateTime time, DepartureTimeSlot slot) {
    LocalTime localTime = time.toLocalTime();
    return !localTime.isBefore(slot.getStartTime())
        && (localTime.isBefore(slot.getEndTime()) || localTime.equals(slot.getEndTime()));
  }

  private Map<UUID, List<StationResponse>> getActiveStationsByDestinationId(List<CoachTrip> trips) {
    List<UUID> destinationIds =
        trips.stream()
            .flatMap(
                trip ->
                    List.of(
                        trip.getRoute().getOriginDestination().getId(),
                        trip.getRoute().getDestinationDestination().getId())
                        .stream())
            .distinct()
            .toList();

    if (destinationIds.isEmpty()) {
      return Map.of();
    }

    Map<UUID, List<StationResponse>> stationsByDestinationId = new HashMap<>();
    stationRepository.findAllByDestinationIdInAndIsDeletedFalse(destinationIds).stream()
        .collect(Collectors.groupingBy(station -> station.getDestination().getId()))
        .forEach(
            (destinationId, stations) ->
                stationsByDestinationId.put(
                    destinationId, coachMapper.toStationResponseList(stations)));

    return stationsByDestinationId;
  }

  private DestinationWithStationsResponse toDestinationWithStationsResponse(
      Destination destination, Map<UUID, List<StationResponse>> stationsByDestinationId) {
    DestinationWithStationsResponse response =
        coachMapper.toDestinationWithStationsResponse(destination);
    if (response == null) {
      return null;
    }
    response.setStations(stationsByDestinationId.getOrDefault(destination.getId(), List.of()));
    return response;
  }
}
