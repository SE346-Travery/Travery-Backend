package com.travery.traverybackend.controllers.tour;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.DestinationResponse;
import com.travery.traverybackend.services.tour.DestinationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/destinations")
@RequiredArgsConstructor
public class DestinationController extends AbstractBaseController {

  private final DestinationService destinationService;

  @GetMapping
  public ResponseEntity<SingleResponse<List<DestinationResponse>>> getAllDestinations() {
    List<DestinationResponse> destinations = destinationService.getAllDestinations();
    return success(destinations, "Fetched destinations successfully");
  }
}
