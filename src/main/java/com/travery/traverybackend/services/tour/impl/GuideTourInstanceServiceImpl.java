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
import com.travery.traverybackend.enums.booking.BookingType;
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
import com.travery.traverybackend.services.tour.GuideTourInstanceService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuideTourInstanceServiceImpl implements GuideTourInstanceService {

  private final TourInstanceRepository tourInstanceRepository;
  private final TourBookingRepository tourBookingRepository;
  private final BookingMemberRepository bookingMemberRepository;
  private final TourIncidentRepository tourIncidentRepository;
  private final UserRepository userRepository;
  private final TourInstanceMapper tourInstanceMapper;
  private final TourIncidentMapper tourIncidentMapper;

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
    TourInstance tourInstance = validateGuideAssignment(guideId, instanceId);

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
    TourInstance tourInstance = validateGuideAssignment(guideId, instanceId);
    if (tourInstance.getStatus() == TourInstanceStatus.CANCELLED
        || tourInstance.getStatus() == TourInstanceStatus.COMPLETED) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Cannot record attendance for a cancelled or completed tour");
    }

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

    Map<UUID, AttendanceStatus> attendanceMap = new HashMap<>();
    for (MemberAttendance attendance : request.getAttendances()) {
      AttendanceStatus existingStatus = attendanceMap.get(attendance.getMemberId());
      if (existingStatus != null && existingStatus != attendance.getStatus()) {
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST,
            "Duplicate member ID "
                + attendance.getMemberId()
                + " provided with different attendance statuses");
      }
      attendanceMap.put(attendance.getMemberId(), attendance.getStatus());
    }

    List<BookingMember> toUpdate = new ArrayList<>();
    for (Map.Entry<UUID, AttendanceStatus> entry : attendanceMap.entrySet()) {
      BookingMember member = membersById.get(entry.getKey());
      if (member == null) {
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST,
            "Member with ID " + entry.getKey() + " does not belong to this tour instance");
      }
      member.setAttendanceStatus(entry.getValue());
      toUpdate.add(member);
    }

    bookingMemberRepository.saveAll(toUpdate);

    return getInstanceDetail(guideId, instanceId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookingMemberResponse> searchPassengers(UUID guideId, UUID instanceId, String query) {
    validateGuideAssignment(guideId, instanceId);
    return bookingMemberRepository.searchInTourInstance(instanceId, query).stream()
        .map(tourInstanceMapper::toBookingMemberResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public GuideTourInstanceDetailResponse updateProgress(
      UUID guideId, UUID instanceId, TourProgressUpdateRequest request) {
    TourInstance tourInstance = validateGuideAssignment(guideId, instanceId);

    if (tourInstance.getStatus() == TourInstanceStatus.CANCELLED
        || tourInstance.getStatus() == TourInstanceStatus.COMPLETED) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Cannot update progress of a cancelled or completed tour");
    }

    TourInstanceStatus newStatus = request.getStatus();
    if (newStatus != TourInstanceStatus.IN_PROGRESS && newStatus != TourInstanceStatus.COMPLETED) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST,
          "Guides are only allowed to update tour status to IN_PROGRESS or COMPLETED");
    }

    tourInstance.setStatus(newStatus);
    tourInstanceRepository.save(tourInstance);
    return getInstanceDetail(guideId, instanceId);
  }

  @Override
  @Transactional
  public TourIncidentResponse reportIncident(
      UUID guideId, UUID instanceId, TourIncidentReportRequest request) {
    TourInstance tourInstance = validateGuideAssignment(guideId, instanceId);

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

  private TourInstance validateGuideAssignment(UUID guideId, UUID instanceId) {
    TourInstance tourInstance =
        tourInstanceRepository
            .findById(instanceId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found"));

    if (tourInstance.getGuide() == null || !tourInstance.getGuide().getId().equals(guideId)) {
      throw new BaseAppException(
          WebErrorCode.FORBIDDEN, "You are not assigned to this tour instance");
    }
    return tourInstance;
  }
}
