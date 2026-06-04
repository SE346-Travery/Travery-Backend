package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.request.booking.CreateReviewRequest;
import com.travery.traverybackend.dtos.response.common.ReviewResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.HotelBookingDetail;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.common.Review;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.enums.common.ReviewTargetType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.mappers.ReviewMapper;
import com.travery.traverybackend.repositories.booking.HotelBookingDetailRepository;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.RouteRepository;
import com.travery.traverybackend.repositories.common.ReviewRepository;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.ReceptionistRepository;
import com.travery.traverybackend.services.common.NotificationService;
import com.travery.traverybackend.services.common.ReviewService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final CoachBookingRepository coachBookingRepository;
  private final RouteRepository routeRepository;
  private final ReviewMapper reviewMapper;
  private final NotificationService notificationService;
  private final ReceptionistRepository receptionistRepository;

  @Override
  @Transactional
  public ReviewResponse createReview(
      UUID bookingId, CreateReviewRequest request, UUID userId, BookingType bookingType) {

    UUID targetId;
    ReviewTargetType targetType;
    User user;

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
    } else if (bookingType == BookingType.HOTEL_BOOKING) {
      HotelBooking booking =
          hotelBookingRepository
              .findByIdAndUser_Id(bookingId, userId)
              .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

      // Khách sạn phải CHECKED_OUT thì mới được đánh giá
      if (booking.getStatus() != BookingStatus.CHECKED_OUT) {
        throw new BaseAppException(BookingErrorCode.REVIEW_TOUR_NOT_COMPLETED);
      }

      // Fetch details với roomType và hotel để tránh N+1 khi lấy targetId
      List<HotelBookingDetail> details =
          hotelBookingDetailRepository.findAllWithRoomTypeAndHotelByHotelBooking_Id(bookingId);

      if (details.isEmpty()) {
        throw new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND);
      }

      targetId = details.get(0).getRoomType().getHotel().getId();
      targetType = ReviewTargetType.HOTEL;
      user = booking.getUser();
    } else if (bookingType == BookingType.COACH_BOOKING) {
      CoachBooking booking =
          coachBookingRepository
              .findByIdAndUser_Id(bookingId, userId)
              .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

      if (booking.getStatus() != BookingStatus.PAID) {
        throw new BaseAppException(BookingErrorCode.REVIEW_BOOKING_NOT_PAID);
      }

      if (booking.getCoachTrip().getStatus() != CoachTripStatus.COMPLETED) {
        throw new BaseAppException(BookingErrorCode.REVIEW_TOUR_NOT_COMPLETED);
      }

      targetId = booking.getCoachTrip().getRoute().getId();
      targetType = ReviewTargetType.ROUTE;
      user = booking.getUser();
    } else {
      throw new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND);
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
    final Review savedReview = reviewRepository.save(review);

    final User finalUser = user;

    // Update target's average rating
    double newAvg = reviewRepository.getAverageRating(targetId, targetType);
    final double finalNewAvg = newAvg;

    if (targetType == ReviewTargetType.HOTEL) {
      hotelRepository
          .findById(targetId)
          .ifPresent(
              h -> {
                h.setAverageRating(finalNewAvg);
                h.setReviewCount(h.getReviewCount() + 1);
                hotelRepository.save(h);

                // Notify Receptionists
                receptionistRepository
                    .findByHotelId(h.getId())
                    .forEach(
                        receptionist -> {
                          notificationService.sendToUser(
                              receptionist.getEmail(),
                              NotificationType.NEW_REVIEW,
                              "Đánh giá khách sạn mới",
                              String.format(
                                  "Người dùng %s đã gửi một đánh giá %d sao cho khách sạn %s.",
                                  finalUser.getFullName(), request.getRating(), h.getName()),
                              savedReview.getId().toString());
                        });
              });
    } else if (targetType == ReviewTargetType.TOUR) {
      tourRepository
          .findById(targetId)
          .ifPresent(
              t -> {
                t.setAverageRating(finalNewAvg);
                t.setReviewCount(t.getReviewCount() + 1);
                tourRepository.save(t);

                // Notify Coordinator and Guide
                if (bookingType == BookingType.TOUR_BOOKING) {
                  TourBooking tourBooking = tourBookingRepository.findById(bookingId).orElse(null);
                  if (tourBooking != null) {
                    java.util.Set<String> staffEmails = new java.util.HashSet<>();
                    if (tourBooking.getTourInstance().getGuide() != null) {
                      staffEmails.add(tourBooking.getTourInstance().getGuide().getEmail());
                    }
                    if (tourBooking.getTourInstance().getCoordinator() != null) {
                      staffEmails.add(tourBooking.getTourInstance().getCoordinator().getEmail());
                    }

                    if (!staffEmails.isEmpty()) {
                      notificationService.sendToUsers(
                          new java.util.ArrayList<>(staffEmails),
                          NotificationType.NEW_REVIEW,
                          "Đánh giá tour mới",
                          String.format(
                              "Người dùng %s đã gửi một đánh giá %d sao cho tour %s.",
                              finalUser.getFullName(), request.getRating(), t.getName()),
                          savedReview.getId().toString());
                    }
                  }
                }
              });
    } else if (targetType == ReviewTargetType.ROUTE) {
      routeRepository
          .findById(targetId)
          .ifPresent(
              r -> {
                r.setAverageRating(newAvg);
                r.setReviewCount(r.getReviewCount() + 1);
                routeRepository.save(r);
              });
    }

    log.info("Review created for {} booking {} on target {}", bookingType, bookingId, targetId);

    return reviewMapper.toReviewResponse(savedReview);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReviewResponse> getReviews(
      UUID targetId, ReviewTargetType targetType, Pageable pageable) {
    Page<Review> reviews =
        reviewRepository.findByTargetIdAndTargetType(targetId, targetType, pageable);
    return reviews.map(reviewMapper::toReviewResponse);
  }
}
