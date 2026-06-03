package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse;
import com.travery.traverybackend.dtos.response.tour.DestinationResponse;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.mappers.DestinationMapper;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.services.common.DestinationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
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
  private final CoachMapper coachMapper;
  private final DestinationMapper destinationMapper;

  @PersistenceContext private EntityManager entityManager;

  @Override
  @Transactional(readOnly = true)
  public List<DestinationWithStationsResponse> searchDestinations(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      List<Destination> destinations = destinationRepository.findAll();
      return coachMapper.toDestinationWithStationsResponseList(destinations);
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

    return coachMapper.toDestinationWithStationsResponseList(results);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DestinationResponse> getAllDestinations() {
    return destinationRepository.findAll().stream()
        .map(destinationMapper::toResponse)
        .collect(Collectors.toList());
  }
}
