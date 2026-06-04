package com.travery.traverybackend.services.tour.impl;

import com.travery.traverybackend.dtos.request.tour.GuideAttendanceRequest;
import com.travery.traverybackend.dtos.request.tour.MemberAttendance;
import com.travery.traverybackend.dtos.request.tour.TourIncidentReportRequest;
import com.travery.traverybackend.dtos.request.tour.TourProgressUpdateRequest;
import com.travery.traverybackend.dtos.response.booking.BookingMemberResponse;
import com.travery.traverybackend.dtos.response.tour.GuideTourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourIncidentResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.tour.TourIncident;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.AttendanceStatus;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.enums.tour.IncidentStatus;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.TourIncidentMapper;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.tour.TourIncidentRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.ChatSessionService;
import com.travery.traverybackend.services.common.NotificationService;
import com.travery.traverybackend.services.tour.GuideTourInstanceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuideTourInstanceServiceImpl implements GuideTourInstanceService {

  private final TourInstanceRepository tourInstanceRepository;
  private final TourBookingRepository tourBookingRepository;
  private final BookingMemberRepository bookingMemberRepository;
  private final TourIncidentRepository tourIncidentRepository;
  private final UserRepository userRepository;
  private final TourInstanceMapper tourInstanceMapper;
  private final TourIncidentMapper tourIncidentMapper;
  private final ChatSessionService chatSessionService;
  private final NotificationService notificationService;

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

  @Override
  @Transactional(readOnly = true)
  public GuideTourInstanceDetailResponse getInstanceDetail(UUID guideId, UUID instanceId) {
    TourInstance tourInstance = getTourInstanceById(instanceId);
    validateGuideOwnership(guideId, tourInstance);

    List<TourBooking> bookings = tourBookingRepository.findByTourInstanceId(instanceId);

    GuideTourInstanceDetailResponse response =
        tourInstanceMapper.toGuideTourInstanceDetailResponse(tourInstance, bookings);

    List<UUID> bookingIds = bookings.stream().map(TourBooking::getId).collect(Collectors.toList());
    if (!bookingIds.isEmpty()) {
      List<BookingMember> bookingMembers =
          bookingMemberRepository.findByBookingIdInAndBookingType(
              bookingIds, BookingType.TOUR_BOOKING);

      Map<UUID, List<BookingMember>> membersByBookingId =
          bookingMembers.stream().collect(Collectors.groupingBy(BookingMember::getBookingId));

      if (response.getBookings() != null) {
        response
            .getBookings()
            .forEach(
                b -> {
                  List<BookingMember> members =
                      membersByBookingId.getOrDefault(b.getId(), List.of());
                  b.setMembers(
                      members.stream()
                          .map(tourInstanceMapper::toBookingMemberResponse)
                          .collect(Collectors.toList()));
                });
      }
    }

    return response;
  }

  @Override
  @Transactional
  public GuideTourInstanceDetailResponse recordAttendance(
      UUID guideId, UUID instanceId, GuideAttendanceRequest request) {
    TourInstance tourInstance = getTourInstanceById(instanceId);
    validateGuideOwnership(guideId, tourInstance);

    List<TourBooking> bookings = tourBookingRepository.findByTourInstanceId(instanceId);
    List<UUID> bookingIds = bookings.stream().map(TourBooking::getId).collect(Collectors.toList());

    if (bookingIds.isEmpty()) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "There are no bookings for this tour instance");
    }

    List<BookingMember> bookingMembers =
        bookingMemberRepository.findByBookingIdInAndBookingType(
            bookingIds, BookingType.TOUR_BOOKING);

    Map<UUID, BookingMember> membersById =
        bookingMembers.stream().collect(Collectors.toMap(BookingMember::getId, member -> member));

    List<BookingMember> toUpdate = new ArrayList<>();
    for (MemberAttendance attendance : request.getAttendances()) {
      BookingMember member = membersById.get(attendance.getMemberId());
      if (member == null) {
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST,
            "Member with ID "
                + attendance.getMemberId()
                + " does not belong to this tour instance");
      }
      member.setAttendanceStatus(attendance.getStatus());
      toUpdate.add(member);
    }

    bookingMemberRepository.saveAll(toUpdate);

    Map<UUID, List<BookingMember>> membersByBooking =
        bookingMembers.stream().collect(Collectors.groupingBy(BookingMember::getBookingId));

    Map<UUID, TourBooking> bookingsById =
        bookings.stream().collect(Collectors.toMap(TourBooking::getId, b -> b));

    List<UUID> usersToRemoveFromChat = new ArrayList<>();

    for (Map.Entry<UUID, List<BookingMember>> entry : membersByBooking.entrySet()) {
      UUID bookingId = entry.getKey();
      List<BookingMember> members = entry.getValue();

      boolean allNoShow =
          members.stream().allMatch(m -> m.getAttendanceStatus() == AttendanceStatus.NO_SHOW);

      if (allNoShow) {
        TourBooking booking = bookingsById.get(bookingId);
        if (booking != null && booking.getUser() != null) {
          usersToRemoveFromChat.add(booking.getUser().getId());
        }
      }
    }

    if (!usersToRemoveFromChat.isEmpty()) {
      try {
        chatSessionService.removeUsersFromChat(instanceId, usersToRemoveFromChat);
      } catch (Exception e) {
        log.error("Failed to batch remove users from chat for instance {}", instanceId, e);
      }
    }

    return getInstanceDetail(guideId, instanceId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookingMemberResponse> searchPassengers(UUID guideId, UUID instanceId, String query) {
    TourInstance tourInstance = getTourInstanceById(instanceId);
    validateGuideOwnership(guideId, tourInstance);
    return bookingMemberRepository.searchInTourInstance(instanceId, query).stream()
        .map(tourInstanceMapper::toBookingMemberResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public GuideTourInstanceDetailResponse updateProgress(
      UUID guideId, UUID instanceId, TourProgressUpdateRequest request) {
    TourInstance tourInstance = getTourInstanceById(instanceId);
    validateGuideOwnership(guideId, tourInstance);

    if (tourInstance.getStatus() == TourInstanceStatus.CANCELLED
        || tourInstance.getStatus() == TourInstanceStatus.COMPLETED) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST,
          "Cannot update status of a cancelled or completed tour instance");
    }

    if (request.getStatus() != TourInstanceStatus.IN_PROGRESS
        && request.getStatus() != TourInstanceStatus.COMPLETED) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Guide can only update status to IN_PROGRESS or COMPLETED");
    }

    tourInstance.setStatus(request.getStatus());
    tourInstanceRepository.save(tourInstance);

    if (request.getStatus() == TourInstanceStatus.COMPLETED) {
      triggerFeedbackNotifications(tourInstance);
    }

    return getInstanceDetail(guideId, instanceId);
  }

  @Override
  @Transactional
  public TourIncidentResponse reportIncident(
      UUID guideId, UUID instanceId, TourIncidentReportRequest request) {
    TourInstance tourInstance = getTourInstanceById(instanceId);
    validateGuideOwnership(guideId, tourInstance);

    User reporter =
        userRepository
            .findById(guideId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

    TourIncident incident =
        TourIncident.builder()
            .tourInstance(tourInstance)
            .reporter(reporter)
            .title(request.getTitle())
            .description(request.getDescription())
            .severity(request.getSeverity())
            .status(IncidentStatus.PENDING)
            .build();

    incident = tourIncidentRepository.save(incident);
    return tourIncidentMapper.toResponse(incident);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TourIncidentResponse> getIncidents(UUID guideId, UUID instanceId) {
    TourInstance tourInstance = getTourInstanceById(instanceId);
    validateGuideOwnership(guideId, tourInstance);

    return tourIncidentRepository.findByTourInstanceId(instanceId).stream()
        .map(tourIncidentMapper::toResponse)
        .collect(Collectors.toList());
  }

  private TourInstance getTourInstanceById(UUID instanceId) {
    return tourInstanceRepository
        .findByIdWithDetails(instanceId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found"));
  }

  private void validateGuideOwnership(UUID guideId, TourInstance tourInstance) {
    if (tourInstance.getGuide() == null || !tourInstance.getGuide().getId().equals(guideId)) {
      throw new BaseAppException(
          WebErrorCode.FORBIDDEN, "You are not assigned to this tour instance");
    }
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
}
