package com.travery.traverybackend.services.coach.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.coach.*;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.user.Guide;
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
  private UUID guideId;

  /** OPEN trip — used for status-transition tests. */
  private CoachTrip openTrip;

  /** IN_PROGRESS trip — required for attendance (check-in / no-show) tests. */
  private CoachTrip inProgressTrip;

  private CoachBooking booking;

  @BeforeEach
  void setUp() {
    tripId = UUID.randomUUID();
    bookingId = UUID.randomUUID();
    guideId = UUID.randomUUID();

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
    Guide guide = Guide.builder().id(guideId).fullName("Guide Name").build();

    openTrip =
        CoachTrip.builder()
            .id(tripId)
            .route(route)
            .coach(coach)
            .driver(driver)
            .guide(guide)
            .status(CoachTripStatus.OPEN)
            .build();

    inProgressTrip =
        CoachTrip.builder()
            .id(tripId)
            .route(route)
            .coach(coach)
            .driver(driver)
            .guide(guide)
            .status(CoachTripStatus.IN_PROGRESS)
            .build();

    // booking belongs to inProgressTrip so attendance methods can proceed past the status gate
    booking =
        CoachBooking.builder()
            .id(bookingId)
            .coachTrip(inProgressTrip)
            .status(BookingStatus.PAID)
            .build();
  }

  // -------------------------------------------------------------------------
  // updateTripStatus
  // -------------------------------------------------------------------------

  @Test
  void updateTripStatus_Success() {
    UpdateCoachTripStatusRequest request =
        new UpdateCoachTripStatusRequest(CoachTripStatus.IN_PROGRESS);

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(openTrip));
    when(coachTripRepository.save(any(CoachTrip.class))).thenReturn(openTrip);
    when(coachMapper.toCoachTripDetailResponse(any())).thenReturn(new CoachTripDetailResponse());

    CoachTripDetailResponse response = guideService.updateTripStatus(guideId, tripId, request);

    assertNotNull(response);
    assertEquals(CoachTripStatus.IN_PROGRESS, openTrip.getStatus());
    verify(coachTripRepository).save(openTrip);
  }

  @Test
  void updateTripStatus_NotAssignedGuide_ThrowsException() {
    UpdateCoachTripStatusRequest request =
        new UpdateCoachTripStatusRequest(CoachTripStatus.IN_PROGRESS);

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(openTrip));

    assertThrows(
        BaseAppException.class,
        () -> guideService.updateTripStatus(UUID.randomUUID(), tripId, request));
    verify(coachTripRepository, never()).save(any(CoachTrip.class));
  }

  // -------------------------------------------------------------------------
  // markPassengerNoShow — service requires IN_PROGRESS trip
  // -------------------------------------------------------------------------

  @Test
  void markPassengerNoShow_Success() {
    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(inProgressTrip));
    // service calls findByIdWithDetails, NOT findById
    when(coachBookingRepository.findByIdWithDetails(bookingId)).thenReturn(Optional.of(booking));

    guideService.markPassengerNoShow(guideId, tripId, bookingId);

    assertEquals(BookingStatus.NO_SHOW, booking.getStatus());
    verify(coachBookingRepository).save(booking);
  }

  @Test
  void markPassengerNoShow_BookingNotPaid_ThrowsException() {
    booking.setStatus(BookingStatus.PENDING);

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(inProgressTrip));
    when(coachBookingRepository.findByIdWithDetails(bookingId)).thenReturn(Optional.of(booking));

    assertThrows(
        BaseAppException.class,
        () -> guideService.markPassengerNoShow(guideId, tripId, bookingId));
  }

  @Test
  void markPassengerNoShow_BookingNotBelongToTrip_ThrowsException() {
    CoachTrip anotherTrip = CoachTrip.builder().id(UUID.randomUUID()).build();
    booking.setCoachTrip(anotherTrip);

    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(inProgressTrip));
    when(coachBookingRepository.findByIdWithDetails(bookingId)).thenReturn(Optional.of(booking));

    assertThrows(
        BaseAppException.class,
        () -> guideService.markPassengerNoShow(guideId, tripId, bookingId));
  }

  @Test
  void markPassengerNoShow_NotAssignedGuide_ThrowsException() {
    when(coachTripRepository.findById(tripId)).thenReturn(Optional.of(inProgressTrip));

    assertThrows(
        BaseAppException.class,
        () -> guideService.markPassengerNoShow(UUID.randomUUID(), tripId, bookingId));
    // booking repo should never be called when guide ownership check fails
    verify(coachBookingRepository, never()).findByIdWithDetails(bookingId);
  }
}
