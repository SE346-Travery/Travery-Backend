package com.travery.traverybackend.services.tour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.request.tour.GuideAttendanceRequest;
import com.travery.traverybackend.dtos.request.tour.MemberAttendance;
import com.travery.traverybackend.dtos.request.tour.TourProgressUpdateRequest;
import com.travery.traverybackend.dtos.response.booking.BookingMemberResponse;
import com.travery.traverybackend.dtos.response.booking.TourBookingResponse;
import com.travery.traverybackend.dtos.response.tour.GuideTourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.AttendanceStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.repositories.booking.BookingMemberRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.tour.impl.GuideTourInstanceServiceImpl;
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
public class GuideTourInstanceServiceTest {

  @Mock private TourInstanceRepository tourInstanceRepository;
  @Mock private TourBookingRepository tourBookingRepository;
  @Mock private BookingMemberRepository bookingMemberRepository;
  @Mock private UserRepository userRepository;
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

  @Test
  void getInstanceDetail_withValidAssignment_returnsDetail() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide guide = Guide.builder().id(guideId).build();
    tourInstance.setGuide(guide);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));
    UUID bookingId = UUID.randomUUID();
    TourBooking tourBooking = TourBooking.builder().id(bookingId).build();
    List<TourBooking> bookings = List.of(tourBooking);
    when(tourBookingRepository.findByTourInstanceId(instanceId)).thenReturn(bookings);

    TourBookingResponse bookingResponse = TourBookingResponse.builder().id(bookingId).build();
    GuideTourInstanceDetailResponse detailResponse =
        GuideTourInstanceDetailResponse.builder().bookings(List.of(bookingResponse)).build();
    when(tourInstanceMapper.toGuideTourInstanceDetailResponse(tourInstance, bookings))
        .thenReturn(detailResponse);

    BookingMember member =
        BookingMember.builder()
            .bookingId(bookingId)
            .bookingType(BookingType.TOUR_BOOKING)
            .fullName("John Doe")
            .build();
    when(bookingMemberRepository.findByBookingIdInAndBookingType(any(), any()))
        .thenReturn(List.of(member));

    BookingMemberResponse memberResponse =
        BookingMemberResponse.builder()
            .fullName("John Doe")
            .attendanceStatus(AttendanceStatus.NOT_CHECKED)
            .build();
    when(tourInstanceMapper.toBookingMemberResponse(member)).thenReturn(memberResponse);

    GuideTourInstanceDetailResponse result =
        guideTourInstanceService.getInstanceDetail(guideId, instanceId);

    assertEquals(detailResponse, result);
    assertEquals(1, result.getBookings().get(0).getMembers().size());
    assertEquals("John Doe", result.getBookings().get(0).getMembers().get(0).getFullName());
  }

  @Test
  void getInstanceDetail_withInvalidAssignment_throwsException() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide otherGuide = Guide.builder().id(UUID.randomUUID()).build();
    tourInstance.setGuide(otherGuide);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));

    assertThrows(
        BaseAppException.class,
        () -> guideTourInstanceService.getInstanceDetail(guideId, instanceId));
  }

  @Test
  void recordAttendance_withValidAssignment_updatesAttendanceStatus() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide guide = Guide.builder().id(guideId).build();
    tourInstance.setGuide(guide);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));

    UUID bookingId = UUID.randomUUID();
    TourBooking tourBooking = TourBooking.builder().id(bookingId).build();
    when(tourBookingRepository.findByTourInstanceId(instanceId)).thenReturn(List.of(tourBooking));

    UUID memberId = UUID.randomUUID();
    BookingMember member =
        BookingMember.builder()
            .id(memberId)
            .bookingId(bookingId)
            .bookingType(BookingType.TOUR_BOOKING)
            .fullName("John Doe")
            .attendanceStatus(AttendanceStatus.NOT_CHECKED)
            .build();
    when(bookingMemberRepository.findByBookingIdInAndBookingType(any(), any()))
        .thenReturn(List.of(member));

    GuideAttendanceRequest request =
        GuideAttendanceRequest.builder()
            .attendances(
                List.of(
                    MemberAttendance.builder()
                        .memberId(memberId)
                        .status(AttendanceStatus.PRESENT)
                        .build()))
            .build();

    // Mock getInstanceDetail invocation inside recordAttendance
    TourBookingResponse bookingResponse = TourBookingResponse.builder().id(bookingId).build();
    GuideTourInstanceDetailResponse detailResponse =
        GuideTourInstanceDetailResponse.builder().bookings(List.of(bookingResponse)).build();
    when(tourInstanceMapper.toGuideTourInstanceDetailResponse(tourInstance, List.of(tourBooking)))
        .thenReturn(detailResponse);

    BookingMemberResponse memberResponse =
        BookingMemberResponse.builder()
            .fullName("John Doe")
            .attendanceStatus(AttendanceStatus.PRESENT)
            .build();
    when(tourInstanceMapper.toBookingMemberResponse(member)).thenReturn(memberResponse);

    GuideTourInstanceDetailResponse result =
        guideTourInstanceService.recordAttendance(guideId, instanceId, request);

    assertEquals(detailResponse, result);
    assertEquals(AttendanceStatus.PRESENT, member.getAttendanceStatus());
  }

  @Test
  void recordAttendance_withCancelledOrCompletedTour_throwsBadRequestException() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide guide = Guide.builder().id(guideId).build();
    tourInstance.setGuide(guide);
    tourInstance.setStatus(TourInstanceStatus.CANCELLED);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));

    GuideAttendanceRequest request =
        GuideAttendanceRequest.builder().attendances(List.of()).build();

    assertThrows(
        BaseAppException.class,
        () -> guideTourInstanceService.recordAttendance(guideId, instanceId, request));
  }

  @Test
  void recordAttendance_withDuplicateMemberIdWithDifferentStatuses_throwsBadRequestException() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide guide = Guide.builder().id(guideId).build();
    tourInstance.setGuide(guide);
    tourInstance.setStatus(TourInstanceStatus.OPEN);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));

    UUID memberId = UUID.randomUUID();
    GuideAttendanceRequest request =
        GuideAttendanceRequest.builder()
            .attendances(
                List.of(
                    MemberAttendance.builder()
                        .memberId(memberId)
                        .status(AttendanceStatus.PRESENT)
                        .build(),
                    MemberAttendance.builder()
                        .memberId(memberId)
                        .status(AttendanceStatus.NO_SHOW)
                        .build()))
            .build();

    assertThrows(
        BaseAppException.class,
        () -> guideTourInstanceService.recordAttendance(guideId, instanceId, request));
  }

  @Test
  void searchPassengers_withNullQuery_returnsAllPassengers() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide guide = Guide.builder().id(guideId).build();
    tourInstance.setGuide(guide);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));

    TourBooking booking = TourBooking.builder().id(UUID.randomUUID()).build();
    when(tourBookingRepository.findByTourInstanceId(instanceId)).thenReturn(List.of(booking));

    BookingMember member = BookingMember.builder().fullName("John Doe").build();
    when(bookingMemberRepository.findByBookingIdInAndBookingType(
            anyList(), eq(BookingType.TOUR_BOOKING)))
        .thenReturn(List.of(member));

    BookingMemberResponse memberResponse =
        BookingMemberResponse.builder().fullName("John Doe").build();
    when(tourInstanceMapper.toBookingMemberResponse(member)).thenReturn(memberResponse);

    List<BookingMemberResponse> result =
        guideTourInstanceService.searchPassengers(guideId, instanceId, null);

    assertEquals(1, result.size());
    assertEquals("John Doe", result.get(0).getFullName());
  }

  @Test
  void updateProgress_withValidAssignment_updatesStatus() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide guide = Guide.builder().id(guideId).build();
    tourInstance.setGuide(guide);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));

    // Mock getInstanceDetail invocation inside updateProgress
    TourBookingResponse bookingResponse = TourBookingResponse.builder().build();
    GuideTourInstanceDetailResponse detailResponse =
        GuideTourInstanceDetailResponse.builder().bookings(List.of(bookingResponse)).build();
    when(tourBookingRepository.findByTourInstanceId(instanceId)).thenReturn(List.of());
    when(tourInstanceMapper.toGuideTourInstanceDetailResponse(any(), any()))
        .thenReturn(detailResponse);

    TourProgressUpdateRequest request =
        new TourProgressUpdateRequest(TourInstanceStatus.IN_PROGRESS);

    GuideTourInstanceDetailResponse result =
        guideTourInstanceService.updateProgress(guideId, instanceId, request);

    assertEquals(detailResponse, result);
    assertEquals(TourInstanceStatus.IN_PROGRESS, tourInstance.getStatus());
    verify(tourInstanceRepository).save(tourInstance);
  }

  @Test
  void updateProgress_withCancelledTour_throwsBadRequestException() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide guide = Guide.builder().id(guideId).build();
    tourInstance.setGuide(guide);
    tourInstance.setStatus(TourInstanceStatus.CANCELLED);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));

    TourProgressUpdateRequest request =
        new TourProgressUpdateRequest(TourInstanceStatus.IN_PROGRESS);

    assertThrows(
        BaseAppException.class,
        () -> guideTourInstanceService.updateProgress(guideId, instanceId, request));
  }

  @Test
  void updateProgress_withInvalidStatusTransition_throwsBadRequestException() {
    UUID guideId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    Guide guide = Guide.builder().id(guideId).build();
    tourInstance.setGuide(guide);
    tourInstance.setStatus(TourInstanceStatus.OPEN);

    when(tourInstanceRepository.findByIdWithDetails(instanceId))
        .thenReturn(Optional.of(tourInstance));

    TourProgressUpdateRequest request = new TourProgressUpdateRequest(TourInstanceStatus.CANCELLED);

    assertThrows(
        BaseAppException.class,
        () -> guideTourInstanceService.updateProgress(guideId, instanceId, request));
  }
}
