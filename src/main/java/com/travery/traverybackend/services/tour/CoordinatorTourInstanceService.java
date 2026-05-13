package com.travery.traverybackend.services.tour;

import com.travery.traverybackend.dtos.request.tour.TourInstanceCreateRequest;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.UserRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoordinatorTourInstanceService {

  private final TourInstanceRepository tourInstanceRepository;
  private final TourRepository tourRepository;
  private final UserRepository userRepository;
  private final TourInstanceMapper tourInstanceMapper;

  @Transactional
  public TourInstanceDetailResponse createInstance(TourInstanceCreateRequest request, UUID coordinatorId) {
    Tour tour = tourRepository.findById(request.getTourId())
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour template not found"));

    Coordinator coordinator = userRepository.findById(coordinatorId)
        .filter(user -> user instanceof Coordinator)
        .map(user -> (Coordinator) user)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.FORBIDDEN, "User is not a coordinator"));

    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Start date must be before end date");
    }

    TourInstance tourInstance = tourInstanceMapper.toEntity(request);
    tourInstance.setTour(tour);
    tourInstance.setCoordinator(coordinator);
    tourInstance.setStatus(TourInstanceStatus.PLANNING);
    
    tourInstance.setMinParticipants(request.getMinParticipants() != null ? request.getMinParticipants() : 10);
    tourInstance.setMaxParticipants(request.getMaxParticipants() != null ? request.getMaxParticipants() : 40);

    TourInstance savedInstance = tourInstanceRepository.save(tourInstance);
    return tourInstanceMapper.toTourInstanceDetailResponse(savedInstance);
  }

  @Transactional(readOnly = true)
  public List<TourInstanceResponse> getInstances(String filter) {
    List<TourInstance> instances;

    switch (filter.toLowerCase()) {
      case "open":
        instances = tourInstanceRepository.findByStatus(TourInstanceStatus.OPEN);
        break;
      case "full":
        instances = tourInstanceRepository.findByStatus(TourInstanceStatus.FULL);
        break;
      case "in_progress":
        instances = tourInstanceRepository.findByStatus(TourInstanceStatus.IN_PROGRESS);
        break;
      case "completed":
        instances = tourInstanceRepository.findByStatus(TourInstanceStatus.COMPLETED);
        break;
      case "waiting_confirmation":
        instances =
            tourInstanceRepository.findWaitingConfirmation(
                List.of(
                    TourInstanceStatus.COMPLETED,
                    TourInstanceStatus.CANCELLED,
                    TourInstanceStatus.PLANNING));
        break;
      case "low_occupancy":
        instances =
            tourInstanceRepository.findLowOccupancy(
                List.of(
                    TourInstanceStatus.COMPLETED,
                    TourInstanceStatus.CANCELLED,
                    TourInstanceStatus.PLANNING));
        break;
      case "all":
      default:
        instances = tourInstanceRepository.findAll();
        break;
    }

    return instances.stream()
        .map(tourInstanceMapper::toTourInstanceResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public TourInstanceDetailResponse getInstanceDetail(UUID id) {
    TourInstance tourInstance =
        tourInstanceRepository
            .findById(id)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found"));
    return tourInstanceMapper.toTourInstanceDetailResponse(tourInstance);
  }
}
