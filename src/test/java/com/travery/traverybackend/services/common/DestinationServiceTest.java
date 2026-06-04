package com.travery.traverybackend.services.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.entities.coach.Station;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.StationRepository;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.services.common.impl.DestinationServiceImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DestinationServiceTest {

  @Mock private DestinationRepository destinationRepository;
  @Mock private StationRepository stationRepository;
  @Mock private CoachMapper coachMapper;

  @InjectMocks private DestinationServiceImpl destinationService;

  @Test
  void searchDestinations_withoutKeyword_setsOnlyNonDeletedStations() {
    UUID destinationId = UUID.randomUUID();
    UUID stationId = UUID.randomUUID();

    Destination destination = Destination.builder().name("Ho Chi Minh").code("HCM").build();
    destination.setId(destinationId);

    Station station =
        Station.builder().name("Station 1").address("123 Main St").destination(destination).build();
    station.setId(stationId);

    DestinationWithStationsResponse destinationResponse =
        DestinationWithStationsResponse.builder()
            .id(destinationId)
            .name("Ho Chi Minh")
            .code("HCM")
            .build();
    StationResponse stationResponse =
        StationResponse.builder()
            .id(stationId)
            .name("Station 1")
            .destinationId(destinationId)
            .build();

    when(destinationRepository.findAll()).thenReturn(List.of(destination));
    when(coachMapper.toDestinationWithStationsResponseList(List.of(destination)))
        .thenReturn(List.of(destinationResponse));
    when(stationRepository.findAllByDestinationIdInAndIsDeletedFalse(List.of(destinationId)))
        .thenReturn(List.of(station));
    when(coachMapper.toStationResponseList(List.of(station))).thenReturn(List.of(stationResponse));

    List<DestinationWithStationsResponse> result = destinationService.searchDestinations(null);

    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getStations().size());
    assertEquals(stationId, result.get(0).getStations().get(0).getId());
    verify(stationRepository).findAllByDestinationIdInAndIsDeletedFalse(List.of(destinationId));
  }
}
