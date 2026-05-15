package com.travery.traverybackend.services.tour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.services.tour.impl.GuideTourInstanceServiceImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GuideTourInstanceServiceTest {

  @Mock private TourInstanceRepository tourInstanceRepository;

  @Mock private TourInstanceMapper tourInstanceMapper;

  @InjectMocks private GuideTourInstanceServiceImpl guideTourInstanceService;

  private TourInstance tourInstance;
  private TourInstanceResponse tourInstanceResponse;

  @BeforeEach
  void setUp() {
    tourInstance = new TourInstance();
    tourInstanceResponse = new TourInstanceResponse();
  }

  @Test
  void getAssignedInstances_withAllFilter_returnsAllAssigned() {
    UUID guideId = UUID.randomUUID();
    when(tourInstanceRepository.findByGuideId(guideId)).thenReturn(List.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceResponse(tourInstance)).thenReturn(tourInstanceResponse);

    List<TourInstanceResponse> result =
        guideTourInstanceService.getAssignedInstances(guideId, "all");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findByGuideId(guideId);
  }

  @Test
  void getAssignedInstances_withOngoingFilter_returnsInProgress() {
    UUID guideId = UUID.randomUUID();
    when(tourInstanceRepository.findByGuideIdAndStatus(guideId, TourInstanceStatus.IN_PROGRESS))
        .thenReturn(List.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceResponse(tourInstance)).thenReturn(tourInstanceResponse);

    List<TourInstanceResponse> result =
        guideTourInstanceService.getAssignedInstances(guideId, "ongoing");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findByGuideIdAndStatus(guideId, TourInstanceStatus.IN_PROGRESS);
  }

  @Test
  void getAssignedInstances_withCompletedFilter_returnsCompleted() {
    UUID guideId = UUID.randomUUID();
    when(tourInstanceRepository.findByGuideIdAndStatus(guideId, TourInstanceStatus.COMPLETED))
        .thenReturn(List.of(tourInstance));
    when(tourInstanceMapper.toTourInstanceResponse(tourInstance)).thenReturn(tourInstanceResponse);

    List<TourInstanceResponse> result =
        guideTourInstanceService.getAssignedInstances(guideId, "completed");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findByGuideIdAndStatus(guideId, TourInstanceStatus.COMPLETED);
  }
}
