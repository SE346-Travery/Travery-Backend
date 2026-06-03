package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.GuideCoachAttendanceRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.exception.error.CoachErrorCode;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingSeatRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.services.coach.GuideCoachTripService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuideCoachTripServiceImpl implements GuideCoachTripService {

  private final CoachTripRepository coachTripRepository;
  private final CoachBookingRepository coachBookingRepository;
  private final CoachBookingSeatRepository coachBookingSeatRepository;
  private final CoachMapper coachMapper;

  @Override
  @Transactional(readOnly = true)
  public List<CoachTripResponse> getAssignedTrips(UUID guideId, String filter) {
    List<CoachTrip> trips;
    if ("active".equalsIgnoreCase(filter)) {
      trips =
          coachTripRepository.findByGuide_IdAndStatusIn(
              guideId, List.of(CoachTripStatus.IN_PROGRESS, CoachTripStatus.OPEN));
    } else if ("history".equalsIgnoreCase(filter)) {
      trips =
          coachTripRepository.findByGuide_IdAndStatusIn(
              guideId, List.of(CoachTripStatus.COMPLETED, CoachTripStatus.CANCELLED));
    } else {
      trips = coachTripRepository.findByGuide_Id(guideId);
    }

    if (trips.isEmpty()) {
      return Collections.emptyList();
    }

    List<UUID> tripIds = trips.stream().map(CoachTrip::getId).collect(Collectors.toList());
    Map<UUID, Long> bookedSeatsMap = new HashMap<>();

    List<Object[]> results =
        coachBookingSeatRepository.countBookedSeatsForTrips(
            tripIds, List.of(BookingStatus.CANCELLED, BookingStatus.NO_SHOW));
    for (Object[] result : results) {
      bookedSeatsMap.put((UUID) result[0], (Long) result[1]);
    }

    return trips.stream()
        .map(
            trip -> {
              int totalSeats =
                  trip.getCoach().getSeatLayout() != null
                      ? trip.getCoach().getSeatLayout().getTotalSeats()
                      : 0;
              long bookedSeats = bookedSeatsMap.getOrDefault(trip.getId(), 0L);
              int availableSeats = totalSeats - (int) bookedSeats;
              return coachMapper.toCoachTripResponse(trip, availableSeats);
            })
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public CoachTripDetailResponse getTripDetail(UUID guideId, UUID tripId) {
    CoachTrip trip = getTripForGuide(guideId, tripId);
    return coachMapper.toCoachTripDetailResponse(trip);
  }

  @Override
  @Transactional
  public CoachTripDetailResponse recordAttendance(
      UUID guideId, UUID tripId, GuideCoachAttendanceRequest request) {
    CoachTrip trip = getTripForGuide(guideId, tripId);

    List<CoachBooking> bookings = coachBookingRepository.findByCoachTrip_Id(tripId);
    for (CoachBooking booking : bookings) {
      if (request.getBookingIds().contains(booking.getId())) {
        if (booking.getStatus() == BookingStatus.PAID
            || booking.getStatus() == BookingStatus.NOT_CHECKED) {
          booking.setStatus(BookingStatus.CHECKED_IN);
        }
      } else {
        if (booking.getStatus() == BookingStatus.PAID) {
          booking.setStatus(BookingStatus.NOT_CHECKED);
        }
      }
    }
    coachBookingRepository.saveAll(bookings);
    return coachMapper.toCoachTripDetailResponse(trip);
  }

  @Override
  @Transactional
  public CoachTripDetailResponse updateTripStatus(
      UUID guideId, UUID tripId, UpdateCoachTripStatusRequest request) {
    CoachTrip trip = getTripForGuide(guideId, tripId);

    if (request.getStatus() == CoachTripStatus.COMPLETED
        || request.getStatus() == CoachTripStatus.IN_PROGRESS) {
      boolean hasNotChecked =
          coachBookingRepository.findByCoachTrip_Id(tripId).stream()
              .anyMatch(b -> b.getStatus() == BookingStatus.NOT_CHECKED);
      if (hasNotChecked) {
        throw new BaseAppException(CoachErrorCode.COACH_TRIP_UPDATE_FAILED);
      }
    }

    trip.setStatus(request.getStatus());
    trip = coachTripRepository.save(trip);
    return coachMapper.toCoachTripDetailResponse(trip);
  }

  private CoachTrip getTripForGuide(UUID guideId, UUID tripId) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));

    if (trip.getGuide() == null || !trip.getGuide().getId().equals(guideId)) {
      throw new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND);
    }
    return trip;
  }
}
