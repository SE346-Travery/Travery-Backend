package com.travery.traverybackend.services.tour;

import com.travery.traverybackend.dtos.request.tour.GuideAttendanceRequest;
import com.travery.traverybackend.dtos.response.tour.GuideTourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import java.util.List;
import java.util.UUID;

public interface GuideTourInstanceService {
  List<TourInstanceResponse> getAssignedInstances(UUID guideId, String filter);

  GuideTourInstanceDetailResponse getInstanceDetail(UUID guideId, UUID instanceId);

  GuideTourInstanceDetailResponse recordAttendance(
      UUID guideId, UUID instanceId, GuideAttendanceRequest request);
}
