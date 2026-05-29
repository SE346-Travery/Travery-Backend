package com.travery.traverybackend.services.tour.impl;

import com.travery.traverybackend.dtos.response.tour.DestinationResponse;
import com.travery.traverybackend.mappers.DestinationMapper;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.services.tour.DestinationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {

  private final DestinationRepository destinationRepository;
  private final DestinationMapper destinationMapper;

  @Override
  @Transactional(readOnly = true)
  public List<DestinationResponse> getAllDestinations() {
    return destinationRepository.findAll().stream()
        .map(destinationMapper::toResponse)
        .collect(Collectors.toList());
  }
}
