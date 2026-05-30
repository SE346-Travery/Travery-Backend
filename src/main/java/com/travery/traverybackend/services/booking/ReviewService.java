package com.travery.traverybackend.services.booking;

import com.travery.traverybackend.dtos.request.booking.CreateReviewRequest;
import com.travery.traverybackend.dtos.response.booking.ReviewResponse;
import com.travery.traverybackend.enums.booking.BookingType;
import java.util.UUID;

public interface ReviewService {

  /**
   * Submit a review for a completed, paid booking. Validates: booking ownership, PAID status, tour
   * COMPLETED, no duplicate review.
   */
  ReviewResponse createReview(
      UUID bookingId, CreateReviewRequest request, UUID userId, BookingType bookingType);
}
