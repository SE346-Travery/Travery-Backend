package com.travery.traverybackend.services.common;

import com.travery.traverybackend.dtos.request.booking.CreateReviewRequest;
import com.travery.traverybackend.dtos.response.common.ReviewResponse;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.common.ReviewTargetType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

  ReviewResponse createReview(
      UUID bookingId, CreateReviewRequest request, UUID userId, BookingType bookingType);

  Page<ReviewResponse> getReviews(UUID targetId, ReviewTargetType targetType, Pageable pageable);
}
