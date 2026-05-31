package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.dtos.request.booking.CreateReviewRequest;
import com.travery.traverybackend.dtos.response.booking.ReviewResponse;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.common.Review;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.common.ReviewTargetType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.common.ReviewRepository;
import com.travery.traverybackend.services.booking.ReviewService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

  private final TourBookingRepository tourBookingRepository;
  private final ReviewRepository reviewRepository;

  @Override
  @Transactional
  public ReviewResponse createReview(UUID bookingId, CreateReviewRequest request, UUID userId) {

    // 1. Load booking and verify ownership
    TourBooking booking =
        tourBookingRepository
            .findByIdAndUser_Id(bookingId, userId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

    // 2. Validate booking is PAID
    if (booking.getStatus() != BookingStatus.PAID) {
      throw new BaseAppException(BookingErrorCode.REVIEW_BOOKING_NOT_PAID);
    }

    // 3. Validate tour instance is COMPLETED
    if (booking.getTourInstance().getStatus() != TourInstanceStatus.COMPLETED) {
      throw new BaseAppException(BookingErrorCode.REVIEW_TOUR_NOT_COMPLETED);
    }

    // 4. Check no duplicate review (DB unique constraint is backup)
    if (reviewRepository.existsByBookingIdAndBookingType(
        booking.getId(), BookingType.TOUR_BOOKING)) {
      throw new BaseAppException(BookingErrorCode.REVIEW_ALREADY_EXISTS);
    }

    // 5. Create review (polymorphic: targetId = tourId, targetType = TOUR)
    UUID tourId = booking.getTourInstance().getTour().getId();
    Review review =
        Review.builder()
            .user(booking.getUser())
            .bookingId(booking.getId())
            .bookingType(BookingType.TOUR_BOOKING)
            .targetId(tourId)
            .targetType(ReviewTargetType.TOUR)
            .averageRating(request.getRating())
            .content(request.getContent())
            .build();
    review = reviewRepository.save(review);

    log.info("Review created for booking {} on tour {}", bookingId, tourId);

    // 6. Build response
    return ReviewResponse.builder()
        .id(review.getId())
        .rating(review.getAverageRating())
        .content(review.getContent())
        .reviewerName(booking.getUser().getFullName())
        .createdAt(review.getCreatedAt())
        .build();
  }
}
