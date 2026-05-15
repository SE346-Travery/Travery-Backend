package com.travery.traverybackend.schedulers;

import com.travery.traverybackend.services.booking.BookingExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Backup scheduler that cleans up expired PENDING bookings every 5 minutes. This is a
 * defense-in-depth mechanism in case Redis misses an expiry event (e.g., Redis restarts and loses
 * keyspace notifications).
 *
 * <p>The primary mechanism is Redis TTL → BookingKeyExpirationListener. This scheduler only catches
 * bookings that slipped through.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryScheduler {

  private final BookingExpiryService bookingExpiryService;

  @Scheduled(fixedRate = 300_000) // every 5 minutes
  public void cleanupExpiredBookings() {
    bookingExpiryService.cleanupExpiredBookings();
  }
}
