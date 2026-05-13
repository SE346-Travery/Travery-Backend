package com.travery.traverybackend.services.tour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.request.tour.TourInstanceCreateRequest;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
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

  @InjectMocks private CoordinatorTourInstanceServiceImpl coordinatorTourInstanceService;

  private TourInstance tourInstance;
  private TourInstanceResponse tourInstanceResponse;
  private TourInstanceDetailResponse tourInstanceDetailResponse;

  @BeforeEach
  void setUp() {
    tourInstance = new TourInstance();
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
            .maxParticipants(30)
            .build();

    Tour tour = new Tour();
    Coordinator coordinator = new Coordinator();

    when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(tourInstanceMapper.toEntity(request)).thenReturn(tourInstance);
    when(tourInstanceRepository.save(any(TourInstance.class))).thenReturn(tourInstance);
    when(tourInstanceMapper.toTourInstanceDetailResponse(tourInstance))
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
    when(tourInstanceRepository.findById(id)).thenReturn(Optional.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceDetailResponse(tourInstance))
        .thenReturn(tourInstanceDetailResponse);

    TourInstanceDetailResponse result = coordinatorTourInstanceService.getInstanceDetail(id);

    assertEquals(tourInstanceDetailResponse, result);
    verify(tourInstanceRepository).findById(id);
  }

  @Test
  void getInstanceDetail_withInvalidId_throwsException() {
    UUID id = UUID.randomUUID();
    when(tourInstanceRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        BaseAppException.class, () -> coordinatorTourInstanceService.getInstanceDetail(id));

    verify(tourInstanceRepository).findById(id);
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
}
