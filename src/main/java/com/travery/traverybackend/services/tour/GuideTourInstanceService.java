package com.travery.traverybackend.services.tour;

import com.travery.traverybackend.dtos.request.tour.GuideAttendanceRequest;
import com.travery.traverybackend.dtos.request.tour.TourIncidentReportRequest;
import com.travery.traverybackend.dtos.request.tour.TourProgressUpdateRequest;
import com.travery.traverybackend.dtos.response.booking.BookingMemberResponse;
import com.travery.traverybackend.dtos.response.tour.GuideTourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourIncidentResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import java.util.List;
import java.util.UUID;

public interface GuideTourInstanceService {
  List<TourInstanceResponse> getAssignedInstances(UUID guideId, String filter);

  GuideTourInstanceDetailResponse getInstanceDetail(UUID guideId, UUID instanceId);

  GuideTourInstanceDetailResponse recordAttendance(
      UUID guideId, UUID instanceId, GuideAttendanceRequest request);

  List<BookingMemberResponse> searchPassengers(UUID guideId, UUID instanceId, String query);

  GuideTourInstanceDetailResponse updateProgress(
      UUID guideId, UUID instanceId, TourProgressUpdateRequest request);

  TourIncidentResponse reportIncident(
      UUID guideId, UUID instanceId, TourIncidentReportRequest request);

  List<TourIncidentResponse> getIncidents(UUID guideId, UUID instanceId);
}
