package com.travery.traverybackend.listeners;

import com.travery.traverybackend.services.booking.BookingExpiryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Listens for Redis keyspace notifications when booking hold keys expire.
 *
 * <p>Flow: booking:hold:{bookingId} key expires after 15-min TTL → Redis publishes expired event →
 * this listener catches it → delegates to BookingExpiryService to cancel the booking in DB.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingKeyExpirationListener implements MessageListener {

  private static final String BOOKING_HOLD_KEY_PREFIX = "booking:hold:";

  private final BookingExpiryService bookingExpiryService;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String expiredKey = message.toString();

    if (!expiredKey.startsWith(BOOKING_HOLD_KEY_PREFIX)) {
      return; // Not a booking hold key, ignore
    }

    String bookingIdStr = expiredKey.substring(BOOKING_HOLD_KEY_PREFIX.length());
    try {
      UUID bookingId = UUID.fromString(bookingIdStr);
      log.info("Redis TTL expired for booking hold: {}", bookingId);
      bookingExpiryService.cancelExpiredBooking(bookingId);
    } catch (IllegalArgumentException e) {
      log.warn("Invalid booking ID in expired Redis key: {}", expiredKey);
    }
  }
}
