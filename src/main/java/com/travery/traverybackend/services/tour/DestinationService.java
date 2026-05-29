package com.travery.traverybackend.services.tour;

import com.travery.traverybackend.dtos.response.tour.DestinationResponse;
import java.util.List;

public interface DestinationService {
  List<DestinationResponse> getAllDestinations();
}
