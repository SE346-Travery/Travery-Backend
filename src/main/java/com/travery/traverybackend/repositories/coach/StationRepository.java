package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.Station;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {

  @EntityGraph(attributePaths = "destination")
  List<Station> findAllByIsDeletedFalse();

  @EntityGraph(attributePaths = "destination")
  Optional<Station> findByIdAndIsDeletedFalse(UUID id);

  @EntityGraph(attributePaths = "destination")
  List<Station> findAllByDestinationIdInAndIsDeletedFalse(List<UUID> destinationIds);
}
