package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.exception.error.CoachErrorCode;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.services.coach.GuideCoachTripService;
import com.travery.traverybackend.services.common.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuideCoachTripServiceImpl implements GuideCoachTripService {

  private final CoachTripRepository coachTripRepository;
  private final CoachBookingRepository coachBookingRepository;
  private final CoachMapper coachMapper;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public CoachTripDetailResponse updateTripStatus(
      UUID tripId, UpdateCoachTripStatusRequest request) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));

    CoachTripStatus oldStatus = trip.getStatus();
    trip.setStatus(request.getStatus());
    trip = coachTripRepository.save(trip);

    if (request.getStatus() == CoachTripStatus.COMPLETED
        && oldStatus != CoachTripStatus.COMPLETED) {
      // Notify all passengers to review
      coachBookingRepository
          .findByCoachTrip_IdAndStatus(tripId, BookingStatus.PAID)
          .forEach(
              booking -> {
                notificationService.sendToUser(
                    booking.getUser().getEmail(),
                    NotificationType.POST_TOUR_REVIEW,
                    "Chuyến đi đã kết thúc!",
                    "Cảm ơn bạn đã đồng hành cùng chúng tôi. Hãy để lại đánh giá cho chuyến xe nhé!",
                    booking.getId().toString());
              });
    }

    return coachMapper.toCoachTripDetailResponse(trip);
  }

  @Override
  @Transactional
  public void markPassengerNoShow(UUID tripId, UUID bookingId) {
    CoachTrip trip =
        coachTripRepository
            .findById(tripId)
            .orElseThrow(() -> new BaseAppException(CoachErrorCode.COACH_TRIP_NOT_FOUND));

    CoachBooking booking =
        coachBookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

    if (!booking.getCoachTrip().getId().equals(trip.getId())) {
      throw new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND);
    }

    if (booking.getStatus() != BookingStatus.PAID) {
      throw new BaseAppException(BookingErrorCode.BOOKING_NOT_PAID);
    }

    booking.setStatus(BookingStatus.NO_SHOW);
    coachBookingRepository.save(booking);
  }
}
