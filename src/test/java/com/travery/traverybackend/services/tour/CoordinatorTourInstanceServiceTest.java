package com.travery.traverybackend.services.tour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CoordinatorTourInstanceServiceTest {

  @Mock private TourInstanceRepository tourInstanceRepository;

  @Mock private TourInstanceMapper tourInstanceMapper;

  @InjectMocks private CoordinatorTourInstanceService coordinatorTourInstanceService;

  private TourInstance tourInstance;
  private TourInstanceResponse tourInstanceResponse;

  @BeforeEach
  void setUp() {
    tourInstance = new TourInstance();
    tourInstanceResponse = new TourInstanceResponse();
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

    List<TourInstanceResponse> result = coordinatorTourInstanceService.getInstances("low_occupancy");

    assertEquals(1, result.size());
    verify(tourInstanceRepository).findLowOccupancy(any());
  }
}
