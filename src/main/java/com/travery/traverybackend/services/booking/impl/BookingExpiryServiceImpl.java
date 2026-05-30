package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
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
  private final HotelBookingRepository hotelBookingRepository;
  private final TourInstanceRepository tourInstanceRepository;
  private final BookingMemberRepository bookingMemberRepository;

  @Override
  @Transactional
  public void cancelExpiredBooking(UUID bookingId) {
    TourBooking tourBooking = tourBookingRepository.findById(bookingId).orElse(null);
    if (tourBooking != null && tourBooking.getStatus() == BookingStatus.PENDING) {
      cancelAndReleaseSeats(tourBooking);
      log.info("TourBooking {} auto-cancelled (payment deadline expired)", bookingId);
      return;
    }

    HotelBooking hotelBooking = hotelBookingRepository.findById(bookingId).orElse(null);
    if (hotelBooking != null && hotelBooking.getStatus() == BookingStatus.PENDING) {
      hotelBooking.setStatus(BookingStatus.CANCELLED);
      hotelBookingRepository.save(hotelBooking);
      log.info("HotelBooking {} auto-cancelled (payment deadline expired)", bookingId);
    }
  }

  @Override
  @Transactional
  public void cleanupExpiredBookings() {
    List<TourBooking> expiredTourBookings =
        tourBookingRepository.findExpiredPendingBookings(LocalDateTime.now());

    for (TourBooking booking : expiredTourBookings) {
      cancelAndReleaseSeats(booking);
    }

    List<HotelBooking> expiredHotelBookings =
        hotelBookingRepository.findExpiredPendingBookings(LocalDateTime.now());

    for (HotelBooking booking : expiredHotelBookings) {
      booking.setStatus(BookingStatus.CANCELLED);
      hotelBookingRepository.save(booking);
    }

    if (!expiredTourBookings.isEmpty() || !expiredHotelBookings.isEmpty()) {
      log.info(
          "Cleanup: cancelled {} tour bookings and {} hotel bookings",
          expiredTourBookings.size(),
          expiredHotelBookings.size());
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
}
