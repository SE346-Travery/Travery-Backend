package com.travery.traverybackend.services.booking;

import java.util.UUID;

public interface BookingExpiryService {

  /**
   * Cancel a single expired booking and release its seats. Called by Redis TTL listener when a
   * booking hold key expires. Must be idempotent — safe to call multiple times for the same
   * bookingId.
   */
  void cancelExpiredBooking(UUID bookingId);

  /**
   * Backup cleanup: scan DB for any PENDING bookings past their payment deadline. Defense-in-depth
   * in case Redis misses an expiry event (e.g., Redis restart).
   */
  void cleanupExpiredBookings();
}
