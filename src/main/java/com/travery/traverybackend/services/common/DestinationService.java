package com.travery.traverybackend.services.common;

import com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse;
import java.util.List;

public interface DestinationService {
  List<DestinationWithStationsResponse> searchDestinations(String keyword);
}
