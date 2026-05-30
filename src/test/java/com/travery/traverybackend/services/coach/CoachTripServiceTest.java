package com.travery.traverybackend.services.coach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.request.coach.SearchCoachTripRequest;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.dtos.response.coach.SeatMapResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.booking.CoachBookingSeat;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.entities.coach.Route;
import com.travery.traverybackend.entities.coach.SeatLayout;
import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import com.travery.traverybackend.entities.coach.Station;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.enums.coach.DepartureTimeSlot;
import com.travery.traverybackend.enums.coach.SeatPosition;
import com.travery.traverybackend.enums.coach.SeatStatus;
import com.travery.traverybackend.enums.coach.SeatTier;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachBookingSeatRepository;
import com.travery.traverybackend.repositories.coach.CoachTripRepository;
import com.travery.traverybackend.repositories.coach.RouteRepository;
import com.travery.traverybackend.repositories.coach.StationRepository;
import com.travery.traverybackend.services.coach.impl.CoachTripServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
class CoachTripServiceTest {

  @Mock private StationRepository stationRepository;
  @Mock private RouteRepository routeRepository;
  @Mock private CoachTripRepository coachTripRepository;
  @Mock private CoachBookingSeatRepository coachBookingSeatRepository;
  @Mock private CoachMapper coachMapper;

  @InjectMocks private CoachTripServiceImpl coachTripService;

  private Station station1;
  private Station station2;
  private Route route;
  private CoachTrip trip1;
  private CoachTrip trip2;
  private SeatLayout layout;
  private SeatLayoutItem seat1;
  private SeatLayoutItem seat2;
  private Coach coach;

  private com.travery.traverybackend.entities.common.Destination dest1;
  private com.travery.traverybackend.entities.common.Destination dest2;

  @BeforeEach
  void setUp() {
    station1 = new Station();
    station1.setId(UUID.randomUUID());
    station1.setName("Station 1");

    station2 = new Station();
    station2.setId(UUID.randomUUID());
    station2.setName("Station 2");

    dest1 = new com.travery.traverybackend.entities.common.Destination();
    dest1.setId(UUID.randomUUID());
    dest1.setName("HN");

    dest2 = new com.travery.traverybackend.entities.common.Destination();
    dest2.setId(UUID.randomUUID());
    dest2.setName("HCM");

    route = new Route();
    route.setId(UUID.randomUUID());
    route.setOriginDestination(dest1);
    route.setDestinationDestination(dest2);
    route.setBasePrice(new BigDecimal("100000"));

    seat1 =
        SeatLayoutItem.builder()
            .id(UUID.randomUUID())
            .seatName("A1")
            .tier(SeatTier.LOWER)
            .position(SeatPosition.FRONT)
            .rowNumber(0)
            .columnNumber(0)
            .build();
    seat2 =
        SeatLayoutItem.builder()
            .id(UUID.randomUUID())
            .seatName("A2")
            .tier(SeatTier.LOWER)
            .position(SeatPosition.FRONT)
            .rowNumber(0)
            .columnNumber(1)
            .build();

    layout =
        SeatLayout.builder()
            .id(UUID.randomUUID())
            .totalSeats(2)
            .items(List.of(seat1, seat2))
            .build();

    coach =
        Coach.builder().id(UUID.randomUUID()).coachType(CoachType.SEAT).seatLayout(layout).build();

    trip1 =
        CoachTrip.builder()
            .id(UUID.randomUUID())
            .route(route)
            .coach(coach)
            .departureTime(LocalDateTime.now().withHour(8))
            .status(CoachTripStatus.OPEN)
            .build();

    trip2 =
        CoachTrip.builder()
            .id(UUID.randomUUID())
            .route(route)
            .coach(coach)
            .departureTime(LocalDateTime.now().withHour(15))
            .status(CoachTripStatus.OPEN)
            .build();
  }

  @Test
  void getStations_returnsList() {
    when(stationRepository.findAll()).thenReturn(List.of(station1, station2));

    StationResponse resp1 =
        StationResponse.builder().id(station1.getId()).name("Station 1").build();
    StationResponse resp2 =
        StationResponse.builder().id(station2.getId()).name("Station 2").build();

    when(coachMapper.toStationResponseList(anyList())).thenReturn(List.of(resp1, resp2));

    List<StationResponse> result = coachTripService.getStations();
    assertEquals(2, result.size());
    verify(stationRepository).findAll();
  }

  @Test
  void searchTrips_validRequest_returnsTrips() {
    SearchCoachTripRequest request =
        SearchCoachTripRequest.builder()
            .originId(dest1.getId())
            .destinationId(dest2.getId())
            .departureDate(LocalDate.now())
            .build();

    LocalDateTime startOfDay = request.getDepartureDate().atStartOfDay();
    LocalDateTime endOfDay = request.getDepartureDate().atTime(LocalTime.MAX);

    when(coachTripRepository.searchTrips(dest1.getId(), dest2.getId(), startOfDay, endOfDay))
        .thenReturn(List.of(trip1, trip2));

    when(coachBookingSeatRepository.countBookedSeatsForTrips(anyList(), anyList()))
        .thenReturn(List.of());

    List<CoachTripResponse> result = coachTripService.searchTrips(request);

    assertEquals(2, result.size());
    // Trip 1 departs at 08:00, Trip 2 at 15:00. Default sort is by departure time
    assertEquals(trip1.getId(), result.get(0).getId());
    assertEquals(2, result.get(0).getAvailableSeats());
  }

  @Test
  void searchTrips_withTimeSlotFilter_returnsFilteredTrips() {
    SearchCoachTripRequest request =
        SearchCoachTripRequest.builder()
            .originId(dest1.getId())
            .destinationId(dest2.getId())
            .departureDate(LocalDate.now())
            .departureTimeSlot(DepartureTimeSlot.MORNING) // 07:00 - 12:00
            .build();

    LocalDateTime startOfDay = request.getDepartureDate().atStartOfDay();
    LocalDateTime endOfDay = request.getDepartureDate().atTime(LocalTime.MAX);

    when(coachTripRepository.searchTrips(dest1.getId(), dest2.getId(), startOfDay, endOfDay))
        .thenReturn(List.of(trip1, trip2)); // trip1 is 8 AM, trip2 is 3 PM

    when(coachBookingSeatRepository.countBookedSeatsForTrips(anyList(), anyList()))
        .thenReturn(List.of());

    List<CoachTripResponse> result = coachTripService.searchTrips(request);

    assertEquals(1, result.size());
    assertEquals(trip1.getId(), result.get(0).getId()); // Only MORNING trip
  }

  @Test
  void searchTrips_invalidRoute_throwsException() {
    SearchCoachTripRequest request =
        SearchCoachTripRequest.builder()
            .originId(dest1.getId())
            .destinationId(dest2.getId())
            .departureDate(LocalDate.now())
            .build();

    LocalDateTime startOfDay = request.getDepartureDate().atStartOfDay();
    LocalDateTime endOfDay = request.getDepartureDate().atTime(LocalTime.MAX);

    when(coachTripRepository.searchTrips(dest1.getId(), dest2.getId(), startOfDay, endOfDay))
        .thenReturn(List.of());

    List<CoachTripResponse> result = coachTripService.searchTrips(request);
    assertEquals(0, result.size());
  }

  @Test
  void getSeatMap_validTrip_returnsSeatMap() {
    when(coachTripRepository.findById(trip1.getId())).thenReturn(Optional.of(trip1));

    // Let's say seat1 is booked
    CoachBookingSeat bookedSeat = new CoachBookingSeat();
    bookedSeat.setId(UUID.randomUUID());
    bookedSeat.setSeatLayoutItem(seat1);

    CoachBooking booking = new CoachBooking();
    booking.setStatus(BookingStatus.PAID);
    bookedSeat.setCoachBooking(booking);

    when(coachBookingSeatRepository.findByTripIdAndBookingStatusNotIn(eq(trip1.getId()), anyList()))
        .thenReturn(List.of(bookedSeat));

    SeatMapResponse result = coachTripService.getSeatMap(trip1.getId());

    assertNotNull(result);
    assertEquals(trip1.getId(), result.getTripId());
    assertEquals(2, result.getTotalSeats());
    assertEquals(1, result.getAvailableSeats());
    assertEquals(2, result.getSeats().size());

    // Seat1 is BOOKED, Seat2 is AVAILABLE
    assertEquals(SeatStatus.BOOKED, result.getSeats().get(0).getStatus());
    assertEquals(SeatStatus.AVAILABLE, result.getSeats().get(1).getStatus());
  }

  @Test
  void getSeatMap_invalidTrip_throwsException() {
    UUID invalidId = UUID.randomUUID();
    when(coachTripRepository.findById(invalidId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> coachTripService.getSeatMap(invalidId));
  }
}
