package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.dtos.request.booking.CreateReviewRequest;
import com.travery.traverybackend.dtos.response.booking.ReviewResponse;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.HotelBookingDetail;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.common.Review;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.common.ReviewTargetType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.repositories.booking.HotelBookingDetailRepository;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.common.ReviewRepository;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.services.booking.ReviewService;
import java.time.LocalDate;
import java.util.List;
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
  private final HotelBookingRepository hotelBookingRepository;
  private final HotelBookingDetailRepository hotelBookingDetailRepository;
  private final ReviewRepository reviewRepository;
  private final HotelRepository hotelRepository;
  private final TourRepository tourRepository;

  @Override
  @Transactional
  public ReviewResponse createReview(
      UUID bookingId, CreateReviewRequest request, UUID userId, BookingType bookingType) {

    UUID targetId;
    ReviewTargetType targetType;
    com.travery.traverybackend.entities.user.User user;

    if (bookingType == BookingType.TOUR_BOOKING) {
      TourBooking booking =
          tourBookingRepository
              .findByIdAndUser_Id(bookingId, userId)
              .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

      if (booking.getStatus() != BookingStatus.PAID) {
        throw new BaseAppException(BookingErrorCode.REVIEW_BOOKING_NOT_PAID);
      }

      if (booking.getTourInstance().getStatus() != TourInstanceStatus.COMPLETED) {
        throw new BaseAppException(BookingErrorCode.REVIEW_TOUR_NOT_COMPLETED);
      }

      targetId = booking.getTourInstance().getTour().getId();
      targetType = ReviewTargetType.TOUR;
      user = booking.getUser();
    } else {
      HotelBooking booking =
          hotelBookingRepository
              .findById(bookingId)
              .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

      if (!booking.getUser().getId().equals(userId)) {
        throw new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND);
      }

      if (booking.getStatus() != BookingStatus.PAID) {
        throw new BaseAppException(BookingErrorCode.REVIEW_BOOKING_NOT_PAID);
      }

      // Check if hotel stay is completed (endDate has passed)
      List<HotelBookingDetail> details =
          hotelBookingDetailRepository.findAllByHotelBooking_Id(bookingId);
      boolean isCompleted =
          details.stream().allMatch(d -> d.getEndDate().isBefore(LocalDate.now()));
      if (!isCompleted) {
        throw new BaseAppException(BookingErrorCode.REVIEW_TOUR_NOT_COMPLETED);
      }

      targetId = details.get(0).getRoomType().getHotel().getId();
      targetType = ReviewTargetType.HOTEL;
      user = booking.getUser();
    }

    if (reviewRepository.existsByBookingIdAndBookingType(bookingId, bookingType)) {
      throw new BaseAppException(BookingErrorCode.REVIEW_ALREADY_EXISTS);
    }

    Review review =
        Review.builder()
            .user(user)
            .bookingId(bookingId)
            .bookingType(bookingType)
            .targetId(targetId)
            .targetType(targetType)
            .averageRating(request.getRating())
            .content(request.getContent())
            .build();
    review = reviewRepository.save(review);

    // Update target's average rating
    double newAvg = reviewRepository.getAverageRating(targetId, targetType);
    if (targetType == ReviewTargetType.HOTEL) {
      hotelRepository
          .findById(targetId)
          .ifPresent(
              h -> {
                h.setAverageRating(newAvg);
                hotelRepository.save(h);
              });
    } else if (targetType == ReviewTargetType.TOUR) {
      tourRepository
          .findById(targetId)
          .ifPresent(
              t -> {
                t.setAverageRating(newAvg);
                tourRepository.save(t);
              });
    }

    log.info("Review created for {} booking {} on target {}", bookingType, bookingId, targetId);

    return ReviewResponse.builder()
        .id(review.getId())
        .rating(review.getAverageRating())
        .content(review.getContent())
        .reviewerName(user.getFullName())
        .createdAt(review.getCreatedAt())
        .build();
  }
}
