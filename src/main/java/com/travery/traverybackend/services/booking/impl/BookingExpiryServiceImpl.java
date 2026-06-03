package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
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
  private final HotelBookingRepository hotelBookingRepository;
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

    HotelBooking hotelBooking = hotelBookingRepository.findById(bookingId).orElse(null);
    if (hotelBooking != null) {
      if (hotelBooking.getStatus() != BookingStatus.PENDING) {
        log.debug("HotelBooking {} skipped — not found or not PENDING", bookingId);
        return;
      }
      hotelBooking.setStatus(BookingStatus.CANCELLED);
      hotelBookingRepository.save(hotelBooking);
      log.info("HotelBooking {} auto-cancelled (payment deadline expired)", bookingId);
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
    LocalDateTime now = LocalDateTime.now();

    // 1. Tour Bookings
    List<TourBooking> expiredTourBookings = tourBookingRepository.findExpiredPendingBookings(now);
    if (!expiredTourBookings.isEmpty()) {
      log.info("Cleanup: found {} expired PENDING tour bookings", expiredTourBookings.size());
      for (TourBooking booking : expiredTourBookings) {
        cancelAndReleaseSeats(booking);
      }
    }

    // 2. Hotel Bookings
    List<HotelBooking> expiredHotelBookings =
        hotelBookingRepository.findExpiredPendingBookings(now);
    if (!expiredHotelBookings.isEmpty()) {
      log.info("Cleanup: found {} expired PENDING hotel bookings", expiredHotelBookings.size());
      for (HotelBooking booking : expiredHotelBookings) {
        booking.setStatus(BookingStatus.CANCELLED);
        hotelBookingRepository.save(booking);
      }
    }

    // 3. Coach Bookings
    List<CoachBooking> expiredCoachBookings =
        coachBookingRepository.findExpiredPendingBookings(now);
    if (!expiredCoachBookings.isEmpty()) {
      log.info("Cleanup: found {} expired PENDING coach bookings", expiredCoachBookings.size());
      for (CoachBooking booking : expiredCoachBookings) {
        cancelAndReleaseCoachSeats(booking);
      }
    }

    if (!expiredTourBookings.isEmpty()
        || !expiredHotelBookings.isEmpty()
        || !expiredCoachBookings.isEmpty()) {
      log.info(
          "Cleanup finished: cancelled {} tour, {} hotel, and {} coach bookings",
          expiredTourBookings.size(),
          expiredHotelBookings.size(),
          expiredCoachBookings.size());
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

    TourInstance tourInstance =
        tourInstanceRepository
            .findByIdWithLock(booking.getTourInstance().getId())
            .orElseThrow(() -> new IllegalStateException("TourInstance not found for booking"));

    // Release participants
    tourInstance.setCurrentParticipants(tourInstance.getCurrentParticipants() - memberCount);

    // If tour was FULL because it was full, re-open it
    if (tourInstance.getStatus() == TourInstanceStatus.FULL
        && tourInstance.getCurrentParticipants() < tourInstance.getTour().getMaxParticipants()) {
      tourInstance.setStatus(TourInstanceStatus.OPEN);
    }

    tourInstanceRepository.save(tourInstance);

    booking.setStatus(BookingStatus.CANCELLED);
    tourBookingRepository.save(booking);
  }

  /** Cancel a coach booking and release its seats. */
  private void cancelAndReleaseCoachSeats(CoachBooking booking) {
    CoachTrip coachTrip =
        coachTripRepository
            .findById(booking.getCoachTrip().getId())
            .orElseThrow(() -> new IllegalStateException("CoachTrip not found for booking"));

    // Release seats logic (assuming seats are tracked via CoachBookingSeat)
    // Here we just mark the booking as CANCELLED.
    // In a real scenario, you'd also update seat availability if tracked in CoachTrip.

    // If trip was FULL because it was full, re-open it
    if (coachTrip.getStatus() == CoachTripStatus.FULL) {
      coachTrip.setStatus(CoachTripStatus.OPEN);
      coachTripRepository.save(coachTrip);
    }

    booking.setStatus(BookingStatus.CANCELLED);
    coachBookingRepository.save(booking);
  }
}
