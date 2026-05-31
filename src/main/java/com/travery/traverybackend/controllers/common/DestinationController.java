package com.travery.traverybackend.controllers.common;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse;
import com.travery.traverybackend.services.common.DestinationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/destinations")
@RequiredArgsConstructor
public class DestinationController extends AbstractBaseController {

  private final DestinationService destinationService;

  @GetMapping("/search")
  public ResponseEntity<SingleResponse<List<DestinationWithStationsResponse>>> searchDestinations(
      @RequestParam String keyword) {
    List<DestinationWithStationsResponse> response = destinationService.searchDestinations(keyword);
    return success(response, "Destinations fetched successfully");
  }
}
