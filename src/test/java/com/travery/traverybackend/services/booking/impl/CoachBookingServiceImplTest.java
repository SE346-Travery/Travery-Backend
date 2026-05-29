package com.travery.traverybackend.services.booking.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.response.coach.CoachBookingSummaryResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingSeatRepository;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CoachBookingServiceImplTest {

  @Mock
  private CoachBookingRepository coachBookingRepository;

  @Mock
  private CoachBookingSeatRepository coachBookingSeatRepository;

  @Mock
  private CoachMapper coachMapper;

  @InjectMocks
  private CoachBookingServiceImpl coachBookingService;

  private UUID userId;
  private UUID bookingId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    bookingId = UUID.randomUUID();
  }

  @Test
  void getMyBookings_ReturnsPageOfSummaries() {
    CoachBooking booking = CoachBooking.builder()
        .id(bookingId)
        .bookedSeats(Collections.emptyList())
        .build();
    Page<CoachBooking> page = new PageImpl<>(Collections.singletonList(booking));

    when(coachBookingRepository.findByUser_IdAndStatus(eq(userId), eq(BookingStatus.PENDING), any()))
        .thenReturn(page);

    when(coachBookingSeatRepository.countSeatsByBookingIds(any()))
        .thenReturn(Collections.emptyList());

    CoachBookingSummaryResponse summary = CoachBookingSummaryResponse.builder().id(bookingId).build();
    when(coachMapper.toCoachBookingSummaryResponse(eq(booking), any(Integer.class)))
        .thenReturn(summary);

    Page<CoachBookingSummaryResponse> result = coachBookingService.getMyBookings(userId, BookingStatus.PENDING,
        PageRequest.of(0, 10));

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(bookingId, result.getContent().get(0).getId());
    verify(coachBookingRepository).findByUser_IdAndStatus(eq(userId), eq(BookingStatus.PENDING), any());
  }
}
