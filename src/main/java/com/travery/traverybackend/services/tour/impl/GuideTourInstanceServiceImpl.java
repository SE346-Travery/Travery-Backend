package com.travery.traverybackend.services.tour.impl;

import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.services.tour.GuideTourInstanceService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuideTourInstanceServiceImpl implements GuideTourInstanceService {

  private final TourInstanceRepository tourInstanceRepository;
  private final TourInstanceMapper tourInstanceMapper;

  @Override
  @Transactional(readOnly = true)
  public List<TourInstanceResponse> getAssignedInstances(UUID guideId, String filter) {
    List<TourInstance> instances;

    switch (filter.toLowerCase()) {
      case "ongoing":
        instances =
            tourInstanceRepository.findByGuideIdAndStatus(guideId, TourInstanceStatus.IN_PROGRESS);
        break;
      case "completed":
        instances =
            tourInstanceRepository.findByGuideIdAndStatus(guideId, TourInstanceStatus.COMPLETED);
        break;
      case "all":
      default:
        instances = tourInstanceRepository.findByGuideId(guideId);
        break;
    }

    return instances.stream()
        .map(tourInstanceMapper::toTourInstanceResponse)
        .collect(Collectors.toList());
  }
}
