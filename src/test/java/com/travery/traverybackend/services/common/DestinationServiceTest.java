package com.travery.traverybackend.services.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.response.tour.DestinationResponse;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.mappers.DestinationMapper;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.services.common.impl.DestinationServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DestinationServiceTest {

  @Mock private DestinationRepository destinationRepository;

  @Mock private DestinationMapper destinationMapper;

  @InjectMocks private DestinationServiceImpl destinationService;

  @Test
  void getAllDestinations_returnsMappedResponses() {
    Destination destination = new Destination();
    destination.setName("Da Lat");
    DestinationResponse response = DestinationResponse.builder().name("Da Lat").build();

    when(destinationRepository.findAll()).thenReturn(List.of(destination));
    when(destinationMapper.toResponse(destination)).thenReturn(response);

    List<DestinationResponse> result = destinationService.getAllDestinations();

    assertEquals(1, result.size());
    assertEquals("Da Lat", result.get(0).getName());
    verify(destinationRepository).findAll();
  }
}
