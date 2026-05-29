package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.services.booking.BookingExpiryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryServiceImpl implements BookingExpiryService {

  private final TourBookingRepository tourBookingRepository;
  private final TourInstanceRepository tourInstanceRepository;
  private final BookingMemberRepository bookingMemberRepository;
  private final CoachBookingRepository coachBookingRepository;
  private final CoachTripRepository coachTripRepository;

  @Override
  @Transactional
  public void cancelExpiredBooking(UUID bookingId) {
    TourBooking tourBooking = tourBookingRepository.findById(bookingId).orElse(null);

    if (tourBooking != null) {
      if (tourBooking.getStatus() != BookingStatus.PENDING) {
        log.debug("TourBooking {} skipped — not found or not PENDING", bookingId);
        return;
      }
      cancelAndReleaseSeats(tourBooking);
      log.info("TourBooking {} auto-cancelled (payment deadline expired)", bookingId);
      return;
    }

    CoachBooking coachBooking = coachBookingRepository.findById(bookingId).orElse(null);
    if (coachBooking != null) {
      if (coachBooking.getStatus() != BookingStatus.PENDING) {
        log.debug("CoachBooking {} skipped — not found or not PENDING", bookingId);
        return;
      }
      cancelAndReleaseCoachSeats(coachBooking);
      log.info("CoachBooking {} auto-cancelled (payment deadline expired)", bookingId);
      return;
    }

    log.debug("Booking {} not found for auto-cancellation", bookingId);
  }

  @Override
  @Transactional
  public void cleanupExpiredBookings() {
    List<TourBooking> expiredBookings =
        tourBookingRepository.findExpiredPendingBookings(LocalDateTime.now());

    if (!expiredBookings.isEmpty()) {
      log.info("Backup cleanup: found {} expired PENDING tour bookings", expiredBookings.size());
      for (TourBooking booking : expiredBookings) {
        cancelAndReleaseSeats(booking);
      }
      log.info("Backup cleanup: cancelled {} expired tour bookings", expiredBookings.size());
    }

    List<CoachBooking> expiredCoachBookings =
        coachBookingRepository.findExpiredPendingBookings(LocalDateTime.now());

    if (!expiredCoachBookings.isEmpty()) {
      log.info("Backup cleanup: found {} expired PENDING coach bookings", expiredCoachBookings.size());
      for (CoachBooking booking : expiredCoachBookings) {
        cancelAndReleaseCoachSeats(booking);
      }
      log.info("Backup cleanup: cancelled {} expired coach bookings", expiredCoachBookings.size());
    }
  }

  /**
   * Cancel a booking and release its seats back to the TourInstance. Acquires PESSIMISTIC_WRITE
   * lock on TourInstance to prevent concurrent participant count corruption.
   */
  private void cancelAndReleaseSeats(TourBooking booking) {
    int memberCount =
        bookingMemberRepository.countByBookingIdAndBookingType(
            booking.getId(), BookingType.TOUR_BOOKING);

    booking.setStatus(BookingStatus.CANCELLED);
    tourBookingRepository.save(booking);

    // Lock TourInstance before modifying participants to prevent race condition
    TourInstance instance =
        tourInstanceRepository.findByIdWithLock(booking.getTourInstance().getId()).orElse(null);

    if (instance == null) {
      log.warn("TourInstance not found for booking {}", booking.getId());
      return;
    }

    int updatedParticipants = Math.max(0, instance.getCurrentParticipants() - memberCount);
    instance.setCurrentParticipants(updatedParticipants);

    if (instance.getStatus() == TourInstanceStatus.FULL) {
      instance.setStatus(TourInstanceStatus.OPEN);
      log.info("TourInstance {} transitioned from FULL → OPEN", instance.getId());
    }
  }

  private void cancelAndReleaseCoachSeats(CoachBooking booking) {
    booking.setStatus(BookingStatus.CANCELLED);
    coachBookingRepository.save(booking);

    CoachTrip trip = coachTripRepository.findByIdForUpdate(booking.getCoachTrip().getId()).orElse(null);
    if (trip == null) {
      log.warn("CoachTrip not found for booking {}", booking.getId());
      return;
    }

    if (trip.getStatus() == CoachTripStatus.FULL) {
      trip.setStatus(CoachTripStatus.OPEN);
      log.info("CoachTrip {} transitioned from FULL → OPEN", trip.getId());
    }
  }
}
