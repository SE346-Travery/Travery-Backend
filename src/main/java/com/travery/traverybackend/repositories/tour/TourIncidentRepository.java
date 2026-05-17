package com.travery.traverybackend.repositories.tour;

import com.travery.traverybackend.entities.tour.TourIncident;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourIncidentRepository extends JpaRepository<TourIncident, UUID> {
  List<TourIncident> findByTourInstanceId(UUID tourInstanceId);
}
