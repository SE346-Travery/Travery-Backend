package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.StationRepository;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.services.common.DestinationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {

  private final DestinationRepository destinationRepository;
  private final StationRepository stationRepository;
  private final CoachMapper coachMapper;

  @PersistenceContext private EntityManager entityManager;

  @Override
  @Transactional(readOnly = true)
  public List<DestinationWithStationsResponse> searchDestinations(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      List<Destination> destinations = destinationRepository.findAll();
      return mapDestinationsWithActiveStations(destinations);
    }

    SearchSession searchSession = Search.session(entityManager);
    var scope = searchSession.scope(Destination.class);

    List<Destination> results =
        searchSession
            .search(scope)
            .where(
                f -> {
                  var b = f.bool();
                  for (String term : keyword.trim().split("\\s+")) {
                    b.must(f.match().field("name").matching(term).fuzzy(1));
                  }
                  return b;
                })
            .fetchHits(50); // Get top 50 matches

    return mapDestinationsWithActiveStations(results);
  }

  private List<DestinationWithStationsResponse> mapDestinationsWithActiveStations(
      List<Destination> destinations) {
    List<DestinationWithStationsResponse> responses =
        coachMapper.toDestinationWithStationsResponseList(destinations);

    if (responses.isEmpty()) {
      return responses;
    }

    Map<UUID, DestinationWithStationsResponse> responsesById =
        responses.stream()
            .collect(Collectors.toMap(DestinationWithStationsResponse::getId, response -> response));

    List<UUID> destinationIds =
        responses.stream().map(DestinationWithStationsResponse::getId).toList();
    Map<UUID, List<StationResponse>> stationsByDestinationId = new HashMap<>();

    stationRepository.findAllByDestinationIdInAndIsDeletedFalse(destinationIds).stream()
        .collect(Collectors.groupingBy(station -> station.getDestination().getId()))
        .forEach(
            (destinationId, stations) ->
                stationsByDestinationId.put(
                    destinationId, coachMapper.toStationResponseList(stations)));

    responsesById.forEach(
        (destinationId, response) ->
            response.setStations(stationsByDestinationId.getOrDefault(destinationId, List.of())));

    return responses;
  }
}
