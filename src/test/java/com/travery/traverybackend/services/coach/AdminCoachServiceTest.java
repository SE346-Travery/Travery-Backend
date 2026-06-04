package com.travery.traverybackend.services.coach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.CreateStationRequest;
import com.travery.traverybackend.dtos.request.coach.SeatLayoutItemRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateStationRequest;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.DriverResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutItemResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.Driver;
import com.travery.traverybackend.entities.coach.SeatLayout;
import com.travery.traverybackend.entities.coach.Station;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.enums.coach.CoachStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.enums.coach.DriverStatus;
import com.travery.traverybackend.enums.coach.SeatPosition;
import com.travery.traverybackend.enums.coach.SeatTier;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachRepository;
import com.travery.traverybackend.repositories.coach.DriverRepository;
import com.travery.traverybackend.repositories.coach.SeatLayoutRepository;
import com.travery.traverybackend.repositories.coach.StationRepository;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.services.coach.impl.AdminCoachServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
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
class AdminCoachServiceTest {

  @Mock private SeatLayoutRepository seatLayoutRepository;
  @Mock private CoachRepository coachRepository;
  @Mock private DriverRepository driverRepository;
  @Mock private StationRepository stationRepository;
  @Mock private DestinationRepository destinationRepository;
  @Mock private CoachMapper coachMapper;

  @InjectMocks private AdminCoachServiceImpl adminCoachService;

  private UUID layoutId;
  private UUID coachId;
  private UUID driverId;
  private UUID stationId;
  private UUID destinationId;
  private SeatLayout seatLayout;
  private Coach coach;
  private Driver driver;
  private Destination destination;
  private Station station;
  private SeatLayoutResponse layoutResponse;
  private CoachResponse coachResponse;
  private DriverResponse driverResponse;
  private StationResponse stationResponse;

  @BeforeEach
  void setUp() {
    layoutId = UUID.randomUUID();
    coachId = UUID.randomUUID();
    driverId = UUID.randomUUID();
    stationId = UUID.randomUUID();
    destinationId = UUID.randomUUID();

    seatLayout =
        SeatLayout.builder()
            .name("Standard 29 Seat")
            .coachType(CoachType.SEAT)
            .totalSeats(29)
            .build();
    seatLayout.setId(layoutId);
    seatLayout.setItems(List.of());

    coach =
        Coach.builder()
            .licensePlate("51B-10000")
            .coachType(CoachType.SEAT)
            .capacity(29)
            .seatLayout(seatLayout)
            .build();
    coach.setId(coachId);

    driver =
        Driver.builder()
            .fullName("John Driver")
            .phoneNumber("0900000000")
            .licenseNumber("GPLX-001")
            .status(DriverStatus.AVAILABLE)
            .build();
    driver.setId(driverId);

    destination = Destination.builder().name("Ho Chi Minh").code("HCM").build();
    destination.setId(destinationId);

    station =
        Station.builder()
            .name("Station 1")
            .address("123 Main St")
            .destination(destination)
            .latitude(new BigDecimal("10.12345678"))
            .longitude(new BigDecimal("106.12345678"))
            .build();
    station.setId(stationId);

    layoutResponse =
        SeatLayoutResponse.builder()
            .id(layoutId)
            .name("Standard 29 Seat")
            .coachType(CoachType.SEAT)
            .totalSeats(29)
            .items(List.of())
            .build();

    coachResponse =
        CoachResponse.builder()
            .id(coachId)
            .licensePlate("51B-10000")
            .coachType(CoachType.SEAT)
            .capacity(29)
            .status(CoachStatus.ACTIVE)
            .seatLayoutName("Standard 29 Seat")
            .build();

    driverResponse =
        DriverResponse.builder()
            .id(driverId)
            .fullName("John Driver")
            .phoneNumber("0900000000")
            .licenseNumber("GPLX-001")
            .status(DriverStatus.AVAILABLE)
            .build();

    stationResponse =
        StationResponse.builder()
            .id(stationId)
            .name("Station 1")
            .address("123 Main St")
            .destinationId(destinationId)
            .destinationName("Ho Chi Minh")
            .latitude(new BigDecimal("10.12345678"))
            .longitude(new BigDecimal("106.12345678"))
            .build();
  }

  // ===== createSeatLayout =====

  @Test
  void createSeatLayout_validRequest_returnsResponse() {
    SeatLayoutItemRequest itemReq =
        SeatLayoutItemRequest.builder()
            .seatName("A1")
            .tier(SeatTier.LOWER)
            .position(SeatPosition.FRONT)
            .rowNumber(0)
            .columnNumber(0)
            .build();

    CreateSeatLayoutRequest request =
        CreateSeatLayoutRequest.builder()
            .name("Standard 29 Seat")
            .coachType(CoachType.SEAT)
            .items(List.of(itemReq))
            .build();

    SeatLayoutResponse expectedResponse =
        SeatLayoutResponse.builder()
            .id(layoutId)
            .name("Standard 29 Seat")
            .coachType(CoachType.SEAT)
            .totalSeats(1)
            .items(
                List.of(
                    SeatLayoutItemResponse.builder()
                        .seatName("A1")
                        .tier(SeatTier.LOWER)
                        .position(SeatPosition.FRONT)
                        .rowNumber(0)
                        .columnNumber(0)
                        .build()))
            .build();

    when(seatLayoutRepository.save(any(SeatLayout.class))).thenReturn(seatLayout);
    when(coachMapper.toSeatLayoutResponse(seatLayout)).thenReturn(expectedResponse);

    SeatLayoutResponse result = adminCoachService.createSeatLayout(request);

    assertNotNull(result);
    assertEquals("Standard 29 Seat", result.getName());
    assertEquals(CoachType.SEAT, result.getCoachType());
    verify(seatLayoutRepository).save(any(SeatLayout.class));
  }

  // ===== getSeatLayouts =====

  @Test
  void getSeatLayouts_withCoachType_returnsFilteredList() {
    when(seatLayoutRepository.findByCoachTypeWithItems(CoachType.SEAT))
        .thenReturn(List.of(seatLayout));
    when(coachMapper.toSeatLayoutResponseList(List.of(seatLayout)))
        .thenReturn(List.of(layoutResponse));

    List<SeatLayoutResponse> result = adminCoachService.getSeatLayouts(CoachType.SEAT);

    assertEquals(1, result.size());
    assertEquals("Standard 29 Seat", result.get(0).getName());
    verify(seatLayoutRepository).findByCoachTypeWithItems(CoachType.SEAT);
  }

  @Test
  void getSeatLayouts_noFilter_returnsAll() {
    when(seatLayoutRepository.findAllWithItems()).thenReturn(List.of(seatLayout));
    when(coachMapper.toSeatLayoutResponseList(List.of(seatLayout)))
        .thenReturn(List.of(layoutResponse));

    List<SeatLayoutResponse> result = adminCoachService.getSeatLayouts(null);

    assertEquals(1, result.size());
    verify(seatLayoutRepository).findAllWithItems();
  }

  // ===== getSeatLayoutDetail =====

  @Test
  void getSeatLayoutDetail_validId_returnsResponse() {
    when(seatLayoutRepository.findByIdWithItems(layoutId)).thenReturn(Optional.of(seatLayout));
    when(coachMapper.toSeatLayoutResponse(seatLayout)).thenReturn(layoutResponse);

    SeatLayoutResponse result = adminCoachService.getSeatLayoutDetail(layoutId);

    assertNotNull(result);
    assertEquals(layoutId, result.getId());
  }

  @Test
  void getSeatLayoutDetail_invalidId_throwsException() {
    when(seatLayoutRepository.findByIdWithItems(layoutId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class, () -> adminCoachService.getSeatLayoutDetail(layoutId));
  }

  // ===== createCoach =====

  @Test
  void createCoach_validRequest_returnsResponse() {
    CreateCoachRequest request =
        CreateCoachRequest.builder()
            .licensePlate("51B-10000")
            .coachType(CoachType.SEAT)
            .capacity(29)
            .seatLayoutId(layoutId)
            .build();

    when(seatLayoutRepository.findById(layoutId)).thenReturn(Optional.of(seatLayout));
    when(coachRepository.save(any(Coach.class))).thenReturn(coach);
    when(coachMapper.toCoachResponse(coach)).thenReturn(coachResponse);

    CoachResponse result = adminCoachService.createCoach(request);

    assertNotNull(result);
    assertEquals("51B-10000", result.getLicensePlate());
    assertEquals("Standard 29 Seat", result.getSeatLayoutName());
    verify(coachRepository).save(any(Coach.class));
  }

  @Test
  void createCoach_invalidLayoutId_throwsException() {
    CreateCoachRequest request =
        CreateCoachRequest.builder()
            .licensePlate("51B-10000")
            .coachType(CoachType.SEAT)
            .capacity(29)
            .seatLayoutId(layoutId)
            .build();

    when(seatLayoutRepository.findById(layoutId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> adminCoachService.createCoach(request));
  }

  // ===== getCoaches =====

  @Test
  void getCoaches_returnsAll() {
    when(coachRepository.findAllByIsDeletedFalse()).thenReturn(List.of(coach));
    when(coachMapper.toCoachResponseList(List.of(coach))).thenReturn(List.of(coachResponse));

    List<CoachResponse> result = adminCoachService.getCoaches();

    assertEquals(1, result.size());
    assertEquals("51B-10000", result.get(0).getLicensePlate());
    verify(coachRepository).findAllByIsDeletedFalse();
  }

  // ===== getCoachDetail =====

  @Test
  void getCoachDetail_validId_returnsResponse() {
    when(coachRepository.findByIdAndIsDeletedFalse(coachId)).thenReturn(Optional.of(coach));
    when(coachMapper.toCoachResponse(coach)).thenReturn(coachResponse);

    CoachResponse result = adminCoachService.getCoachDetail(coachId);

    assertNotNull(result);
    assertEquals(coachId, result.getId());
  }

  @Test
  void getCoachDetail_invalidId_throwsException() {
    when(coachRepository.findByIdAndIsDeletedFalse(coachId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> adminCoachService.getCoachDetail(coachId));
  }

  @Test
  void updateCoach_withPartialRequest_updatesOnlyProvidedFields() {
    UUID newLayoutId = UUID.randomUUID();
    SeatLayout newLayout =
        SeatLayout.builder()
            .name("Sleeper 40 Seat")
            .coachType(CoachType.BED)
            .totalSeats(40)
            .build();
    newLayout.setId(newLayoutId);

    UpdateCoachRequest request =
        UpdateCoachRequest.builder()
            .licensePlate("51B-20000")
            .status(CoachStatus.MAINTENANCE)
            .seatLayoutId(newLayoutId)
            .build();

    when(coachRepository.findByIdAndIsDeletedFalse(coachId)).thenReturn(Optional.of(coach));
    when(seatLayoutRepository.findById(newLayoutId)).thenReturn(Optional.of(newLayout));
    when(coachRepository.save(coach)).thenReturn(coach);
    when(coachMapper.toCoachResponse(coach)).thenReturn(coachResponse);

    adminCoachService.updateCoach(coachId, request);

    assertEquals("51B-20000", coach.getLicensePlate());
    assertEquals(CoachType.SEAT, coach.getCoachType());
    assertEquals(CoachStatus.MAINTENANCE, coach.getStatus());
    assertEquals(newLayout, coach.getSeatLayout());
    verify(coachRepository).save(coach);
  }

  @Test
  void deleteCoach_setsDeletedTrue() {
    when(coachRepository.findByIdAndIsDeletedFalse(coachId)).thenReturn(Optional.of(coach));

    adminCoachService.deleteCoach(coachId);

    assertEquals(true, coach.isDeleted());
    verify(coachRepository).save(coach);
  }

  @Test
  void createDriver_validRequest_returnsResponse() {
    CreateDriverRequest request =
        CreateDriverRequest.builder()
            .fullName("John Driver")
            .phoneNumber("0900000000")
            .licenseNumber("GPLX-001")
            .build();

    when(driverRepository.save(any(Driver.class))).thenReturn(driver);
    when(coachMapper.toDriverResponse(driver)).thenReturn(driverResponse);

    DriverResponse result = adminCoachService.createDriver(request);

    assertEquals(driverId, result.getId());
    verify(driverRepository).save(any(Driver.class));
  }

  @Test
  void getDrivers_returnsNonDeletedDrivers() {
    when(driverRepository.findAllByIsDeletedFalse()).thenReturn(List.of(driver));
    when(coachMapper.toDriverResponseList(List.of(driver))).thenReturn(List.of(driverResponse));

    List<DriverResponse> result = adminCoachService.getDrivers();

    assertEquals(1, result.size());
    verify(driverRepository).findAllByIsDeletedFalse();
  }

  @Test
  void updateDriver_withPartialRequest_updatesOnlyProvidedFields() {
    UpdateDriverRequest request =
        UpdateDriverRequest.builder().fullName("Jane Driver").status(DriverStatus.ON_LEAVE).build();

    when(driverRepository.findByIdAndIsDeletedFalse(driverId)).thenReturn(Optional.of(driver));
    when(driverRepository.save(driver)).thenReturn(driver);
    when(coachMapper.toDriverResponse(driver)).thenReturn(driverResponse);

    adminCoachService.updateDriver(driverId, request);

    assertEquals("Jane Driver", driver.getFullName());
    assertEquals("0900000000", driver.getPhoneNumber());
    assertEquals(DriverStatus.ON_LEAVE, driver.getStatus());
    verify(driverRepository).save(driver);
  }

  @Test
  void deleteDriver_setsDeletedTrue() {
    when(driverRepository.findByIdAndIsDeletedFalse(driverId)).thenReturn(Optional.of(driver));

    adminCoachService.deleteDriver(driverId);

    assertEquals(true, driver.isDeleted());
    verify(driverRepository).save(driver);
  }

  @Test
  void createStation_validRequest_returnsResponse() {
    CreateStationRequest request =
        CreateStationRequest.builder()
            .name("Station 1")
            .address("123 Main St")
            .destinationId(destinationId)
            .latitude(new BigDecimal("10.12345678"))
            .longitude(new BigDecimal("106.12345678"))
            .build();

    when(destinationRepository.findById(destinationId)).thenReturn(Optional.of(destination));
    when(stationRepository.save(any(Station.class))).thenReturn(station);
    when(coachMapper.toStationResponse(station)).thenReturn(stationResponse);

    StationResponse result = adminCoachService.createStation(request);

    assertEquals(stationId, result.getId());
    verify(stationRepository).save(any(Station.class));
  }

  @Test
  void getStations_returnsNonDeletedStations() {
    when(stationRepository.findAllByIsDeletedFalse()).thenReturn(List.of(station));
    when(coachMapper.toStationResponseList(List.of(station))).thenReturn(List.of(stationResponse));

    List<StationResponse> result = adminCoachService.getStations();

    assertEquals(1, result.size());
    verify(stationRepository).findAllByIsDeletedFalse();
  }

  @Test
  void updateStation_withPartialRequest_updatesOnlyProvidedFields() {
    UpdateStationRequest request =
        UpdateStationRequest.builder().name("Station 2").destinationId(destinationId).build();

    when(stationRepository.findByIdAndIsDeletedFalse(stationId)).thenReturn(Optional.of(station));
    when(destinationRepository.findById(destinationId)).thenReturn(Optional.of(destination));
    when(stationRepository.save(station)).thenReturn(station);
    when(coachMapper.toStationResponse(station)).thenReturn(stationResponse);

    adminCoachService.updateStation(stationId, request);

    assertEquals("Station 2", station.getName());
    assertEquals("123 Main St", station.getAddress());
    assertEquals(destination, station.getDestination());
    verify(stationRepository).save(station);
  }

  @Test
  void deleteStation_setsDeletedTrue() {
    when(stationRepository.findByIdAndIsDeletedFalse(stationId)).thenReturn(Optional.of(station));

    adminCoachService.deleteStation(stationId);

    assertEquals(true, station.isDeleted());
    verify(stationRepository).save(station);
  }
}
