package com.travery.traverybackend.schedulers;

import com.travery.traverybackend.services.booking.BookingExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Primary scheduler that cancels expired PENDING bookings every 5 minutes. Queries the database for
 * bookings where paymentDeadline has passed and status is still PENDING.
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
