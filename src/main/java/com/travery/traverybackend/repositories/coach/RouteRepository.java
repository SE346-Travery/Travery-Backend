package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.Route;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {

  Optional<Route> findByOriginDestinationIdAndDestinationDestinationId(
      UUID originId, UUID destinationId);

  @EntityGraph(attributePaths = {"originDestination", "destinationDestination", "refundPolicy"})
  List<Route> findAllByIsDeletedFalse();

  @EntityGraph(attributePaths = {"originDestination", "destinationDestination", "refundPolicy"})
  Optional<Route> findByIdAndIsDeletedFalse(UUID id);

  @EntityGraph(attributePaths = {"originDestination", "destinationDestination", "refundPolicy"})
  Optional<Route> findByOriginDestinationIdAndDestinationDestinationIdAndIsDeletedFalse(
      UUID originId, UUID destinationId);
}
