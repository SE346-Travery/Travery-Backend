package com.travery.traverybackend.services.coach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.SeatLayoutItemRequest;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutItemResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.SeatLayout;
import com.travery.traverybackend.enums.coach.CoachStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.enums.coach.SeatPosition;
import com.travery.traverybackend.enums.coach.SeatTier;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachRepository;
import com.travery.traverybackend.repositories.coach.SeatLayoutRepository;
import com.travery.traverybackend.services.coach.impl.AdminCoachServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
  @Mock private CoachMapper coachMapper;

  @InjectMocks private AdminCoachServiceImpl adminCoachService;

  private UUID layoutId;
  private UUID coachId;
  private SeatLayout seatLayout;
  private Coach coach;
  private SeatLayoutResponse layoutResponse;
  private CoachResponse coachResponse;

  @BeforeEach
  void setUp() {
    layoutId = UUID.randomUUID();
    coachId = UUID.randomUUID();

    seatLayout = SeatLayout.builder().name("Standard 29 Seat").coachType(CoachType.SEAT).totalSeats(29).build();
    seatLayout.setId(layoutId);
    seatLayout.setItems(List.of());

    coach = Coach.builder().licensePlate("51B-10000").coachType(CoachType.SEAT).capacity(29).seatLayout(seatLayout).build();
    coach.setId(coachId);

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
        EntityNotFoundException.class,
        () -> adminCoachService.getSeatLayoutDetail(layoutId));
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

    assertThrows(
        EntityNotFoundException.class, () -> adminCoachService.createCoach(request));
  }

  // ===== getCoaches =====

  @Test
  void getCoaches_returnsAll() {
    when(coachRepository.findAll()).thenReturn(List.of(coach));
    when(coachMapper.toCoachResponseList(List.of(coach))).thenReturn(List.of(coachResponse));

    List<CoachResponse> result = adminCoachService.getCoaches();

    assertEquals(1, result.size());
    assertEquals("51B-10000", result.get(0).getLicensePlate());
  }

  // ===== getCoachDetail =====

  @Test
  void getCoachDetail_validId_returnsResponse() {
    when(coachRepository.findById(coachId)).thenReturn(Optional.of(coach));
    when(coachMapper.toCoachResponse(coach)).thenReturn(coachResponse);

    CoachResponse result = adminCoachService.getCoachDetail(coachId);

    assertNotNull(result);
    assertEquals(coachId, result.getId());
  }

  @Test
  void getCoachDetail_invalidId_throwsException() {
    when(coachRepository.findById(coachId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class, () -> adminCoachService.getCoachDetail(coachId));
  }
}
