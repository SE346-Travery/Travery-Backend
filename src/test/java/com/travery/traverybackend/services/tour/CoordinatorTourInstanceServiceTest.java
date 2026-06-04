package com.travery.traverybackend.services.tour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.request.tour.TourInstanceCreateRequest;
import com.travery.traverybackend.dtos.request.tour.TourInstanceUpdateRequest;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachRepository;
import com.travery.traverybackend.repositories.coach.DriverRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.ChatSessionService;
import com.travery.traverybackend.services.tour.impl.CoordinatorTourInstanceServiceImpl;
import java.time.LocalDate;
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
public class CoordinatorTourInstanceServiceTest {

  @Mock private TourInstanceRepository tourInstanceRepository;

  @Mock private TourRepository tourRepository;

  @Mock private UserRepository userRepository;

  @Mock private TourInstanceMapper tourInstanceMapper;
  @Mock private CoachRepository coachRepository;
  @Mock private DriverRepository driverRepository;
  @Mock private HotelBookingRepository hotelBookingRepository;
  @Mock private ChatSessionService chatSessionService;

  @InjectMocks private CoordinatorTourInstanceServiceImpl coordinatorTourInstanceService;

  private TourInstance tourInstance;
  private TourInstanceResponse tourInstanceResponse;
  private TourInstanceDetailResponse tourInstanceDetailResponse;

  @BeforeEach
  void setUp() {
    tourInstance = new TourInstance();
    tourInstance.setStartDate(LocalDate.now().plusDays(10));
    tourInstance.setEndDate(LocalDate.now().plusDays(15));
    tourInstanceResponse = new TourInstanceResponse();
    tourInstanceDetailResponse = new TourInstanceDetailResponse();
  }

  @Test
  void createInstance_withValidData_returnsDetail() {
    UUID tourId = UUID.randomUUID();
    UUID coordinatorId = UUID.randomUUID();
    TourInstanceCreateRequest request =
        TourInstanceCreateRequest.builder()
            .tourId(tourId)
            .startDate(LocalDate.now().plusDays(10))
            .endDate(LocalDate.now().plusDays(15))
            .build();

    Tour tour = new Tour();
    Coordinator coordinator = new Coordinator();

    when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(tourInstanceMapper.toEntity(request)).thenReturn(tourInstance);
    when(tourInstanceRepository.save(any(TourInstance.class))).thenReturn(tourInstance);
    when(tourInstanceMapper.toCoordinatorTourInstanceDetailResponse(tourInstance))
        .thenReturn(tourInstanceDetailResponse);

    TourInstanceDetailResponse result =
        coordinatorTourInstanceService.createInstance(request, coordinatorId);

    assertEquals(tourInstanceDetailResponse, result);
    verify(tourInstanceRepository).save(any(TourInstance.class));
  }

  @Test
  void createInstance_withInvalidDates_throwsException() {
    UUID tourId = UUID.randomUUID();
    UUID coordinatorId = UUID.randomUUID();
    TourInstanceCreateRequest request =
        TourInstanceCreateRequest.builder()
            .tourId(tourId)
            .startDate(LocalDate.now().plusDays(15))
            .endDate(LocalDate.now().plusDays(10))
            .build();

    Tour tour = new Tour();
    Coordinator coordinator = new Coordinator();

    when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));

    assertThrows(
        BaseAppException.class,
        () -> coordinatorTourInstanceService.createInstance(request, coordinatorId));
  }

  @Test
  void createInstance_withNonCoordinator_throwsException() {
    UUID tourId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    TourInstanceCreateRequest request = TourInstanceCreateRequest.builder().tourId(tourId).build();

    Tour tour = new Tour();
    Tourist tourist = new Tourist();

    when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
    when(userRepository.findById(userId)).thenReturn(Optional.of(tourist));

    assertThrows(
        BaseAppException.class,
        () -> coordinatorTourInstanceService.createInstance(request, userId));
  }

  @Test
  void getInstances_withAllFilter_returnsAllInstances() {
    when(tourInstanceRepository.findAll()).thenReturn(List.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceResponse(tourInstance)).thenReturn(tourInstanceResponse);

    List<TourInstanceResponse> result = coordinatorTourInstanceService.getInstances("all");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findAll();
  }

  @Test
  void getInstances_withOpenFilter_returnsOpenInstances() {
    when(tourInstanceRepository.findByStatus(TourInstanceStatus.OPEN))
        .thenReturn(List.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceResponse(tourInstance)).thenReturn(tourInstanceResponse);

    List<TourInstanceResponse> result = coordinatorTourInstanceService.getInstances("open");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findByStatus(TourInstanceStatus.OPEN);
  }

  @Test
  void getInstances_withWaitingConfirmationFilter_returnsWaitingInstances() {
    when(tourInstanceRepository.findWaitingConfirmation(any())).thenReturn(List.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceResponse(tourInstance)).thenReturn(tourInstanceResponse);

    List<TourInstanceResponse> result =
        coordinatorTourInstanceService.getInstances("waiting_confirmation");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findWaitingConfirmation(any());
  }

  @Test
  void getInstances_withLowOccupancyFilter_returnsLowOccupancyInstances() {
    when(tourInstanceRepository.findLowOccupancy(any())).thenReturn(List.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceResponse(tourInstance)).thenReturn(tourInstanceResponse);

    List<TourInstanceResponse> result =
        coordinatorTourInstanceService.getInstances("low_occupancy");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findLowOccupancy(any());
  }

  @Test
  void getInstanceDetail_withValidId_returnsDetail() {
    UUID id = UUID.randomUUID();
    when(tourInstanceRepository.findByIdWithDetails(id)).thenReturn(Optional.of(tourInstance));
    when(tourInstanceMapper.toCoordinatorTourInstanceDetailResponse(tourInstance))
        .thenReturn(tourInstanceDetailResponse);

    TourInstanceDetailResponse result = coordinatorTourInstanceService.getInstanceDetail(id);

    assertEquals(tourInstanceDetailResponse, result);
    verify(tourInstanceRepository).findByIdWithDetails(id);
  }

  @Test
  void getInstanceDetail_withInvalidId_throwsException() {
    UUID id = UUID.randomUUID();
    when(tourInstanceRepository.findByIdWithDetails(id)).thenReturn(Optional.empty());

    assertThrows(
        BaseAppException.class, () -> coordinatorTourInstanceService.getInstanceDetail(id));

    verify(tourInstanceRepository).findByIdWithDetails(id);
  }

  @Test
  void getInstances_withUnknownFilter_defaultsToAll() {
    when(tourInstanceRepository.findAll()).thenReturn(List.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceResponse(tourInstance)).thenReturn(tourInstanceResponse);

    List<TourInstanceResponse> result =
        coordinatorTourInstanceService.getInstances("unknown_random_filter");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findAll();
  }

  @Test
  void updateInstance_withValidData_updatesFields() {
    UUID id = UUID.randomUUID();
    UUID guideId = UUID.randomUUID();
    UUID oldGuideId = UUID.randomUUID();
    TourInstanceUpdateRequest request =
        TourInstanceUpdateRequest.builder()
            .guideId(guideId)
            .status(TourInstanceStatus.OPEN)
            .build();

    Guide oldGuide = new Guide();
    oldGuide.setId(oldGuideId);
    tourInstance.setGuide(oldGuide);
    tourInstance.setStatus(TourInstanceStatus.PLANNING);

    Guide guide = new Guide();
    guide.setId(guideId);
    when(tourInstanceRepository.findById(id)).thenReturn(Optional.of(tourInstance));
    when(userRepository.findById(guideId)).thenReturn(Optional.of(guide));
    when(tourInstanceRepository.save(any(TourInstance.class))).thenReturn(tourInstance);
    when(tourInstanceMapper.toCoordinatorTourInstanceDetailResponse(tourInstance))
        .thenReturn(tourInstanceDetailResponse);

    TourInstanceDetailResponse result = coordinatorTourInstanceService.updateInstance(id, request);

    assertEquals(tourInstanceDetailResponse, result);
    assertEquals(guide, tourInstance.getGuide());
    assertEquals(TourInstanceStatus.OPEN, tourInstance.getStatus());
    verify(chatSessionService).removeUserFromChat(id, oldGuideId);
    verify(chatSessionService).addUserToChat(id, guideId);
  }

  @Test
  void updateInstance_withInvalidInstanceId_throwsException() {
    UUID id = UUID.randomUUID();
    TourInstanceUpdateRequest request = new TourInstanceUpdateRequest();

    when(tourInstanceRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        BaseAppException.class, () -> coordinatorTourInstanceService.updateInstance(id, request));
  }

  @Test
  void updateInstance_operationalFieldWhileNotPlanning_throwsException() {
    UUID id = UUID.randomUUID();
    UUID guideId = UUID.randomUUID();
    TourInstanceUpdateRequest request =
        TourInstanceUpdateRequest.builder().guideId(guideId).build();

    tourInstance.setStatus(TourInstanceStatus.OPEN);
    when(tourInstanceRepository.findById(id)).thenReturn(Optional.of(tourInstance));

    assertThrows(
        BaseAppException.class, () -> coordinatorTourInstanceService.updateInstance(id, request));
  }

  @Test
  void updateInstance_withNewFields_updatesCorrectly() {
    UUID id = UUID.randomUUID();
    UUID coordinatorId = UUID.randomUUID();
    UUID oldCoordinatorId = UUID.randomUUID();
    LocalDate newStart = LocalDate.now().plusDays(20);

    TourInstanceUpdateRequest request =
        TourInstanceUpdateRequest.builder()
            .coordinatorId(coordinatorId)
            .startDate(newStart)
            .build();

    Coordinator oldCoordinator = new Coordinator();
    oldCoordinator.setId(oldCoordinatorId);
    tourInstance.setCoordinator(oldCoordinator);

    Coordinator coordinator = new Coordinator();
    coordinator.setId(coordinatorId);
    tourInstance.setStatus(TourInstanceStatus.PLANNING);
    tourInstance.setEndDate(LocalDate.now().plusDays(25)); // Ensure valid date range
    tourInstance.setStartDate(LocalDate.now().plusDays(10));

    when(tourInstanceRepository.findById(id)).thenReturn(Optional.of(tourInstance));
    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(tourInstanceRepository.save(any())).thenReturn(tourInstance);
    when(tourInstanceMapper.toCoordinatorTourInstanceDetailResponse(any()))
        .thenReturn(tourInstanceDetailResponse);

    coordinatorTourInstanceService.updateInstance(id, request);

    assertEquals(coordinator, tourInstance.getCoordinator());
    assertEquals(newStart, tourInstance.getStartDate());
    verify(chatSessionService).removeUserFromChat(id, oldCoordinatorId);
    verify(chatSessionService).addUserToChat(id, coordinatorId);
  }

  @Test
  void updateInstance_withDeletedCoach_throwsException() {
    UUID id = UUID.randomUUID();
    UUID coachId = UUID.randomUUID();
    TourInstanceUpdateRequest request =
        TourInstanceUpdateRequest.builder().coachId(coachId).build();

    Coach oldCoach = new Coach();
    oldCoach.setId(UUID.randomUUID());
    tourInstance.setCoach(oldCoach);
    tourInstance.setStatus(TourInstanceStatus.PLANNING);
    when(tourInstanceRepository.findById(id)).thenReturn(Optional.of(tourInstance));
    when(coachRepository.findByIdAndIsDeletedFalse(coachId)).thenReturn(Optional.empty());

    assertThrows(
        BaseAppException.class, () -> coordinatorTourInstanceService.updateInstance(id, request));
  }

  @Test
  void updateInstance_withDeletedDriver_throwsException() {
    UUID id = UUID.randomUUID();
    UUID driverId = UUID.randomUUID();
    TourInstanceUpdateRequest request =
        TourInstanceUpdateRequest.builder().driverId(driverId).build();

    tourInstance.setStatus(TourInstanceStatus.PLANNING);
    when(tourInstanceRepository.findById(id)).thenReturn(Optional.of(tourInstance));
    when(driverRepository.findByIdAndIsDeletedFalse(driverId)).thenReturn(Optional.empty());

    assertThrows(
        BaseAppException.class, () -> coordinatorTourInstanceService.updateInstance(id, request));
  }
}
