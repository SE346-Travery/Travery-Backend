package com.travery.traverybackend.services.tour.impl;

import com.travery.traverybackend.dtos.request.tour.TourInstanceCreateRequest;
import com.travery.traverybackend.dtos.request.tour.TourInstanceUpdateRequest;
import com.travery.traverybackend.dtos.request.tour.TourProgressUpdateRequest;
import com.travery.traverybackend.dtos.response.tour.TourIncidentResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.TourIncidentMapper;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachRepository;
import com.travery.traverybackend.repositories.coach.DriverRepository;
import com.travery.traverybackend.repositories.tour.TourIncidentRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.ChatSessionService;
import com.travery.traverybackend.services.common.NotificationService;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.services.tour.CoordinatorTourInstanceService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoordinatorTourInstanceServiceImpl implements CoordinatorTourInstanceService {

  private final TourInstanceRepository tourInstanceRepository;
  private final TourRepository tourRepository;
  private final UserRepository userRepository;
  private final CoachRepository coachRepository;
  private final DriverRepository driverRepository;
  private final HotelBookingRepository hotelBookingRepository;
  private final TourIncidentRepository tourIncidentRepository;
  private final TourInstanceMapper tourInstanceMapper;
  private final TourIncidentMapper tourIncidentMapper;
  private final ChatSessionService chatSessionService;
  private final TourBookingRepository tourBookingRepository;
  private final NotificationService notificationService;

  @Override
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

  @Override
  @Transactional(readOnly = true)
  public TourInstanceDetailResponse getInstanceDetail(UUID id) {
    TourInstance tourInstance =
        tourInstanceRepository
            .findByIdWithDetails(id)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found"));
    return tourInstanceMapper.toCoordinatorTourInstanceDetailResponse(tourInstance);
  }

  @Override
  @Transactional
  public TourInstanceDetailResponse createInstance(
      TourInstanceCreateRequest request, UUID coordinatorId) {
    Tour tour =
        tourRepository
            .findById(request.getTourId())
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour template not found"));

    Coordinator coordinator =
        userRepository
            .findById(coordinatorId)
            .filter(user -> user instanceof Coordinator)
            .map(user -> (Coordinator) user)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.FORBIDDEN, "User is not a coordinator"));

    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Start date must be before or equal to end date");
    }

    TourInstance tourInstance = tourInstanceMapper.toEntity(request);
    tourInstance.setTour(tour);
    tourInstance.setCoordinator(coordinator);
    tourInstance.setStatus(TourInstanceStatus.PLANNING);

    TourInstance savedInstance = tourInstanceRepository.save(tourInstance);
    return tourInstanceMapper.toCoordinatorTourInstanceDetailResponse(savedInstance);
  }

  @Override
  @Transactional
  public TourInstanceDetailResponse updateInstance(UUID id, TourInstanceUpdateRequest request) {
    TourInstance tourInstance =
        tourInstanceRepository
            .findById(id)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found"));

    if (isOperationalUpdate(request) && tourInstance.getStatus() != TourInstanceStatus.PLANNING) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST,
          "Operational fields can only be updated when status is PLANNING");
    }

    if (request.getCoordinatorId() != null) {
      Coordinator coordinator =
          userRepository
              .findById(request.getCoordinatorId())
              .filter(user -> user instanceof Coordinator)
              .map(user -> (Coordinator) user)
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Coordinator not found"));

      if (tourInstance.getCoordinator() != null
          && !tourInstance.getCoordinator().getId().equals(coordinator.getId())) {
        try {
          chatSessionService.removeUserFromChat(id, tourInstance.getCoordinator().getId());
        } catch (Exception e) {
          log.error(
              "Failed to remove old coordinator {} from chat",
              tourInstance.getCoordinator().getId(),
              e);
        }
      }

      tourInstance.setCoordinator(coordinator);

      try {
        chatSessionService.addUserToChat(id, coordinator.getId());
      } catch (Exception e) {
        log.error("Failed to add new coordinator {} to chat", coordinator.getId(), e);
      }
    }

    if (request.getGuideId() != null) {
      Guide guide =
          userRepository
              .findById(request.getGuideId())
              .filter(user -> user instanceof Guide)
              .map(user -> (Guide) user)
              .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Guide not found"));

      if (tourInstance.getGuide() != null
          && !tourInstance.getGuide().getId().equals(guide.getId())) {
        try {
          chatSessionService.removeUserFromChat(id, tourInstance.getGuide().getId());
        } catch (Exception e) {
          log.error("Failed to remove old guide {} from chat", tourInstance.getGuide().getId(), e);
        }
      }

      tourInstance.setGuide(guide);

      try {
        chatSessionService.addUserToChat(id, guide.getId());
      } catch (Exception e) {
        log.error("Failed to add new guide {} to chat", guide.getId(), e);
      }
    }

    if (request.getCoachId() != null) {
      tourInstance.setCoach(
          coachRepository
              .findById(request.getCoachId())
              .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Coach not found")));
    }

    if (request.getDriverId() != null) {
      tourInstance.setDriver(
          driverRepository
              .findById(request.getDriverId())
              .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Driver not found")));
    }

    if (request.getHotelBookingId() != null) {
      tourInstance.setHotelBooking(
          hotelBookingRepository
              .findById(request.getHotelBookingId())
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel booking not found")));
    }

    if (request.getStartDate() != null) {
      tourInstance.setStartDate(request.getStartDate());
    }

    if (request.getEndDate() != null) {
      tourInstance.setEndDate(request.getEndDate());
    }

    if (tourInstance.getStartDate().isAfter(tourInstance.getEndDate())) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Start date must be before or equal to end date");
    }

    if (request.getStatus() != null) {
      validateStatusTransition(tourInstance, request.getStatus());
      tourInstance.setStatus(request.getStatus());
      if (request.getStatus() == TourInstanceStatus.COMPLETED) {
        triggerFeedbackNotifications(tourInstance);
      }
    }

    TourInstance savedInstance = tourInstanceRepository.save(tourInstance);
    return tourInstanceMapper.toCoordinatorTourInstanceDetailResponse(savedInstance);
  }

  @Override
  @Transactional
  public TourInstanceDetailResponse updateStatus(UUID id, TourProgressUpdateRequest request) {
    TourInstance tourInstance =
        tourInstanceRepository
            .findById(id)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found"));

    validateStatusTransition(tourInstance, request.getStatus());
    tourInstance.setStatus(request.getStatus());

    if (request.getStatus() == TourInstanceStatus.COMPLETED) {
      triggerFeedbackNotifications(tourInstance);
    }

    TourInstance savedInstance = tourInstanceRepository.save(tourInstance);
    return tourInstanceMapper.toCoordinatorTourInstanceDetailResponse(savedInstance);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TourIncidentResponse> getIncidents(UUID instanceId) {
    if (!tourInstanceRepository.existsById(instanceId)) {
      throw new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found");
    }
    return tourIncidentRepository.findByTourInstanceId(instanceId).stream()
        .map(tourIncidentMapper::toResponse)
        .collect(Collectors.toList());
  }

  private void triggerFeedbackNotifications(TourInstance instance) {
    tourBookingRepository
        .findByTourInstanceIdAndStatus(instance.getId(), BookingStatus.PAID)
        .forEach(
            booking -> {
              notificationService.sendToUser(
                  booking.getUser().getEmail(),
                  NotificationType.POST_TOUR_REVIEW,
                  "Chuyến đi kết thúc",
                  String.format(
                      "Hy vọng bạn đã có trải nghiệm tuyệt vời với %s. Hãy để lại đánh giá của bạn nhé!",
                      instance.getTour().getName()),
                  instance.getId().toString());
            });
  }

  private void validateStatusTransition(TourInstance tourInstance, TourInstanceStatus newStatus) {
    TourInstanceStatus currentStatus = tourInstance.getStatus();

    if (currentStatus == newStatus) {
      return;
    }

    switch (currentStatus) {
      case PLANNING:
        if (newStatus != TourInstanceStatus.OPEN && newStatus != TourInstanceStatus.CANCELLED) {
          throw new BaseAppException(
              WebErrorCode.BAD_REQUEST, "Planning instance can only move to OPEN or CANCELLED");
        }
        break;
      case OPEN:
        if (newStatus != TourInstanceStatus.FULL
            && newStatus != TourInstanceStatus.IN_PROGRESS
            && newStatus != TourInstanceStatus.CANCELLED) {
          throw new BaseAppException(
              WebErrorCode.BAD_REQUEST,
              "Open instance can only move to FULL, IN_PROGRESS or CANCELLED");
        }
        break;
      case FULL:
        if (newStatus != TourInstanceStatus.IN_PROGRESS
            && newStatus != TourInstanceStatus.CANCELLED) {
          throw new BaseAppException(
              WebErrorCode.BAD_REQUEST, "Full instance can only move to IN_PROGRESS or CANCELLED");
        }
        break;
      case IN_PROGRESS:
        if (newStatus != TourInstanceStatus.COMPLETED) {
          throw new BaseAppException(
              WebErrorCode.BAD_REQUEST, "Ongoing instance can only move to COMPLETED");
        }
        break;
      case COMPLETED:
      case CANCELLED:
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST, "Cannot change status of a " + currentStatus + " instance");
      default:
        throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Invalid status transition");
    }
  }

  private boolean isOperationalUpdate(TourInstanceUpdateRequest request) {
    return request.getCoordinatorId() != null
        || request.getGuideId() != null
        || request.getCoachId() != null
        || request.getDriverId() != null
        || request.getHotelBookingId() != null
        || request.getStartDate() != null
        || request.getEndDate() != null;
  }
}
