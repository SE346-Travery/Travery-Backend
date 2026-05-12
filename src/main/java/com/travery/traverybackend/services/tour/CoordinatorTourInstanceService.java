package com.travery.traverybackend.services.tour;

import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import java.util.List;
import java.util.UUID;

public interface CoordinatorTourInstanceService {
  List<TourInstanceResponse> getInstances(String filter);

  TourInstanceDetailResponse getInstanceDetail(UUID id);
}
