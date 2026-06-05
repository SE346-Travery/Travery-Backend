package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.dtos.response.coach.GuideBookingResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.exception.error.CoachErrorCode;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingSeatRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.services.coach.GuideCoachTripService;
import com.travery.traverybackend.services.common.NotificationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuideCoachTripServiceImpl implements GuideCoachTripService {

  // Valid transitions guide is allowed to make
  private static final Map<CoachTripStatus, Set<CoachTripStatus>> ALLOWED_TRANSITIONS =
      Map.of(
          CoachTripStatus.OPEN, Set.of(CoachTripStatus.IN_PROGRESS),
          CoachTripStatus.FULL, Set.of(CoachTripStatus.IN_PROGRESS),
          CoachTripStatus.IN_PROGRESS, Set.of(CoachTripStatus.COMPLETED));

  private final CoachTripRepository coachTripRepository;
  private final CoachBookingRepository coachBookingRepository;
  private final CoachBookingSeatRepository coachBookingSeatRepository;
  private final CoachMapper coachMapper;
  private final NotificationService notificationService;

  @Override
  @Transactional(readOnly = true)
  public Page<CoachTripResponse> getMyTrips(
      UUID guideId, CoachTripStatus status, Pageable pageable) {
    Page<CoachTrip> tripPage;
    if (status != null) {
      tripPage = coachTripRepository.findByGuide_IdAndStatus(guideId, status, pageable);
    } else {
      tripPage = coachTripRepository.findByGuide_Id(guideId, pageable);
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
  public CoachTripDetailResponse getTripDetail(UUID guideId, UUID tripId) {
    CoachTrip trip =
        coachTripRepository
            .findByIdWithDetails(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));
    validateAssignedGuide(guideId, trip);
    return coachMapper.toCoachTripDetailResponse(trip);
  }

  @Override
  @Transactional(readOnly = true)
  public List<GuideBookingResponse> getTripAttendance(UUID guideId, UUID tripId) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));
    validateAssignedGuide(guideId, trip);

    List<CoachBooking> bookings = coachBookingRepository.findAttendanceListByTripId(tripId);

    return bookings.stream().map(coachMapper::toGuideBookingResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public CoachTripDetailResponse updateTripStatus(
      UUID guideId, UUID tripId, UpdateCoachTripStatusRequest request) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));
    validateAssignedGuide(guideId, trip);
    validateStatusTransition(trip.getStatus(), request.getStatus());

    CoachTripStatus oldStatus = trip.getStatus();
    trip.setStatus(request.getStatus());
    trip = coachTripRepository.save(trip);

    if (request.getStatus() == CoachTripStatus.COMPLETED
        && oldStatus != CoachTripStatus.COMPLETED) {
      // Notify all passengers to review
      coachBookingRepository
          .findByCoachTrip_IdAndStatus(tripId, BookingStatus.PAID)
          .forEach(
              booking -> {
                notificationService.sendToUser(
                    booking.getUser().getEmail(),
                    NotificationType.POST_TOUR_REVIEW,
                    "Chuyến đi đã kết thúc!",
                    "Cảm ơn bạn đã đồng hành cùng chúng tôi. Hãy để lại đánh giá cho chuyến xe nhé!",
                    booking.getId().toString());
              });
    }

    return coachMapper.toCoachTripDetailResponse(trip);
  }

  @Override
  @Transactional
  public void checkInBooking(UUID guideId, UUID tripId, UUID bookingId) {
    CoachTrip trip = resolveInProgressTrip(guideId, tripId);

    CoachBooking booking = resolveBookingForAttendance(bookingId, trip);
    booking.setStatus(BookingStatus.CHECKED_IN);
    coachBookingRepository.save(booking);
  }

  @Override
  @Transactional
  public void markPassengerNoShow(UUID guideId, UUID tripId, UUID bookingId) {
    CoachTrip trip = resolveInProgressTrip(guideId, tripId);

    CoachBooking booking = resolveBookingForAttendance(bookingId, trip);
    booking.setStatus(BookingStatus.NO_SHOW);
    coachBookingRepository.save(booking);
  }

  // ---------------------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------------------

  /** Validates that the guide is assigned to this trip. */
  private void validateAssignedGuide(UUID guideId, CoachTrip trip) {
    if (trip.getGuide() == null || !trip.getGuide().getId().equals(guideId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN);
    }
  }

  /** Validates a status transition against the allowed state machine. */
  private void validateStatusTransition(CoachTripStatus current, CoachTripStatus next) {
    Set<CoachTripStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
    if (!allowed.contains(next)) {
      throw new BaseAppException(CoachErrorCode.INVALID_STATUS_TRANSITION);
    }
  }

  /**
   * Resolves the trip and verifies it is IN_PROGRESS before any attendance action. Shared by
   * checkInBooking and markPassengerNoShow.
   */
  private CoachTrip resolveInProgressTrip(UUID guideId, UUID tripId) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));
    validateAssignedGuide(guideId, trip);
    if (trip.getStatus() != CoachTripStatus.IN_PROGRESS) {
      throw new BaseAppException(CoachErrorCode.TRIP_NOT_IN_PROGRESS);
    }
    return trip;
  }

  /**
   * Resolves a PAID booking that belongs to the given trip. Used for both check-in and no-show
   * operations. Uses findByIdWithDetails to avoid lazy-loading CoachTrip for the ownership check.
   */
  private CoachBooking resolveBookingForAttendance(UUID bookingId, CoachTrip trip) {
    // findByIdWithDetails eagerly fetches coachTrip to avoid a lazy-load hit on getId()
    CoachBooking booking =
        coachBookingRepository
            .findByIdWithDetails(bookingId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

    if (!booking.getCoachTrip().getId().equals(trip.getId())) {
      throw new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND);
    }

    if (booking.getStatus() != BookingStatus.PAID) {
      throw new BaseAppException(BookingErrorCode.BOOKING_NOT_PAID);
    }

    return booking;
  }
}
