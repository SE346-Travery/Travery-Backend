package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
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
  private final TourInstanceRepository tourInstanceRepository;
  private final BookingMemberRepository bookingMemberRepository;

  @Override
  @Transactional
  public void cancelExpiredBooking(UUID bookingId) {
    TourBooking booking = tourBookingRepository.findById(bookingId).orElse(null);

    // Idempotent: if booking not found or already cancelled/paid, do nothing
    if (booking == null || booking.getStatus() != BookingStatus.PENDING) {
      log.debug("Booking {} skipped — not found or not PENDING", bookingId);
      return;
    }

    cancelAndReleaseSeats(booking);
    log.info("Booking {} auto-cancelled (payment deadline expired)", bookingId);
  }

  @Override
  @Transactional
  public void cleanupExpiredBookings() {
    List<TourBooking> expiredBookings =
        tourBookingRepository.findExpiredPendingBookings(LocalDateTime.now());

    if (expiredBookings.isEmpty()) {
      return;
    }

    log.info("Backup cleanup: found {} expired PENDING bookings", expiredBookings.size());
    for (TourBooking booking : expiredBookings) {
      cancelAndReleaseSeats(booking);
    }
    log.info("Backup cleanup: cancelled {} expired bookings", expiredBookings.size());
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
}
