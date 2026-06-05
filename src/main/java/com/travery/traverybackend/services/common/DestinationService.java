package com.travery.traverybackend.services.common;

import com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse;
import com.travery.traverybackend.dtos.response.tour.DestinationResponse;
import java.util.List;

public interface DestinationService {
  List<DestinationWithStationsResponse> searchDestinations(String keyword);

  List<DestinationResponse> getAllDestinations();
}
