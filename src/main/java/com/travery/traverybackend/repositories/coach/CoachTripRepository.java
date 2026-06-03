package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachTripRepository extends JpaRepository<CoachTrip, UUID> {

  @Query(
      "SELECT ct FROM CoachTrip ct "
          + "JOIN FETCH ct.route r "
          + "JOIN FETCH r.originDestination "
          + "JOIN FETCH r.destinationDestination "
          + "JOIN FETCH ct.coach c "
          + "JOIN FETCH c.seatLayout "
          + "WHERE ct.route.id = :routeId "
          + "AND ct.departureTime >= :startOfDay "
          + "AND ct.departureTime < :endOfDay "
          + "AND ct.status = 'OPEN'")
  List<CoachTrip> findByRouteIdAndDate(
      UUID routeId, LocalDateTime startOfDay, LocalDateTime endOfDay);

  @Query(
      "SELECT ct FROM CoachTrip ct "
          + "JOIN FETCH ct.route r "
          + "JOIN FETCH r.originDestination os "
          + "JOIN FETCH r.destinationDestination ds "
          + "JOIN FETCH ct.coach c "
          + "JOIN FETCH c.seatLayout "
          + "WHERE os.id = :originId "
          + "AND ds.id = :destinationId "
          + "AND ct.departureTime >= :startOfDay "
          + "AND ct.departureTime < :endOfDay "
          + "AND ct.status = 'OPEN'")
  List<CoachTrip> searchTrips(
      UUID originId, UUID destinationId, LocalDateTime startOfDay, LocalDateTime endOfDay);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT ct FROM CoachTrip ct WHERE ct.id = :id")
  Optional<CoachTrip> findByIdForUpdate(UUID id);

  @EntityGraph(
      attributePaths = {
        "coach",
        "coach.seatLayout",
        "route",
        "route.originDestination",
        "route.destinationDestination",
        "driver"
      })
  Page<CoachTrip> findByCoordinator_Id(UUID coordinatorId, Pageable pageable);

  @EntityGraph(
      attributePaths = {
        "coach",
        "coach.seatLayout",
        "route",
        "route.originDestination",
        "route.destinationDestination",
        "driver"
      })
  Page<CoachTrip> findByCoordinator_IdAndStatus(
      UUID coordinatorId, CoachTripStatus status, Pageable pageable);

  @EntityGraph(
      attributePaths = {
        "coach",
        "coach.seatLayout",
        "route",
        "route.originDestination",
        "route.destinationDestination",
        "driver"
      })
  List<CoachTrip> findByGuide_Id(UUID guideId);

  @EntityGraph(
      attributePaths = {
        "coach",
        "coach.seatLayout",
        "route",
        "route.originDestination",
        "route.destinationDestination",
        "driver"
      })
  List<CoachTrip> findByGuide_IdAndStatusIn(UUID guideId, List<CoachTripStatus> statuses);
}
