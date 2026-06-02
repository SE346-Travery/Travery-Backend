package com.travery.traverybackend.services.tour;

import com.travery.traverybackend.dtos.request.tour.TourInstanceCreateRequest;
import com.travery.traverybackend.dtos.request.tour.TourInstanceUpdateRequest;
import com.travery.traverybackend.dtos.request.tour.TourProgressUpdateRequest;
import com.travery.traverybackend.dtos.response.tour.TourIncidentResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import java.util.List;
import java.util.UUID;

public interface CoordinatorTourInstanceService {
  List<TourInstanceResponse> getInstances(String filter);

  TourInstanceDetailResponse createInstance(TourInstanceCreateRequest request, UUID coordinatorId);

  TourInstanceDetailResponse getInstanceDetail(UUID id);

  TourInstanceDetailResponse updateInstance(UUID id, TourInstanceUpdateRequest request);

  TourInstanceDetailResponse updateStatus(UUID id, TourProgressUpdateRequest request);

  List<TourIncidentResponse> getIncidents(UUID instanceId);

  void deleteInstance(UUID id, UUID coordinatorId);
}
