package com.travery.traverybackend.services.coach.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.coach.*;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuideCoachTripServiceImplTest {

  @Mock private CoachTripRepository coachTripRepository;
  @Mock private CoachBookingRepository coachBookingRepository;
  @Mock private CoachBookingSeatRepository coachBookingSeatRepository;
  @Mock private CoachMapper coachMapper;

  @InjectMocks private GuideCoachTripServiceImpl guideService;

  private UUID tripId;
  private UUID bookingId;
  private CoachTrip trip;
  private CoachBooking booking;

  @BeforeEach
  void setUp() {
    tripId = UUID.randomUUID();
    bookingId = UUID.randomUUID();

    Route route =
        Route.builder()
            .id(UUID.randomUUID())
            .originDestination(new Destination())
            .destinationDestination(new Destination())
            .build();
    SeatLayout seatLayout = SeatLayout.builder().totalSeats(40).items(List.of()).build();
    Coach coach =
        Coach.builder()
            .id(UUID.randomUUID())
            .coachType(CoachType.BED)
            .seatLayout(seatLayout)
            .build();
    Driver driver = Driver.builder().id(UUID.randomUUID()).fullName("John Doe").build();

    trip =
        CoachTrip.builder()
            .id(tripId)
            .route(route)
            .coach(coach)
            .driver(driver)
            .status(CoachTripStatus.OPEN)
            .build();

    booking =
        CoachBooking.builder().id(bookingId).coachTrip(trip).status(BookingStatus.PAID).build();
  }

  @Test
  void updateTripStatus_Success() {
    UpdateCoachTripStatusRequest request =
        new UpdateCoachTripStatusRequest(CoachTripStatus.IN_PROGRESS);

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(coachTripRepository.save(any(CoachTrip.class))).thenReturn(trip);
    when(coachMapper.toCoachTripDetailResponse(any())).thenReturn(new CoachTripDetailResponse());

    CoachTripDetailResponse response = guideService.updateTripStatus(tripId, request);

    assertNotNull(response);
    assertEquals(CoachTripStatus.IN_PROGRESS, trip.getStatus());
    verify(coachTripRepository).save(trip);
  }

  @Test
  void markPassengerNoShow_Success() {
    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(coachBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

    guideService.markPassengerNoShow(tripId, bookingId);

    assertEquals(BookingStatus.NO_SHOW, booking.getStatus());
    verify(coachBookingRepository).save(booking);
  }

  @Test
  void markPassengerNoShow_BookingNotPaid_ThrowsException() {
    booking.setStatus(BookingStatus.PENDING);

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(coachBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

    assertThrows(BaseAppException.class, () -> guideService.markPassengerNoShow(tripId, bookingId));
  }

  @Test
  void markPassengerNoShow_BookingNotBelongToTrip_ThrowsException() {
    CoachTrip anotherTrip = CoachTrip.builder().id(UUID.randomUUID()).build();
    booking.setCoachTrip(anotherTrip);

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(coachBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

    assertThrows(BaseAppException.class, () -> guideService.markPassengerNoShow(tripId, bookingId));
  }
}
