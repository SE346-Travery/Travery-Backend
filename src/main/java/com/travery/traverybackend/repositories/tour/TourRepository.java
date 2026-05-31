package com.travery.traverybackend.repositories.tour;

import com.travery.traverybackend.entities.tour.Tour;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, UUID>, TourSearchCustomRepository {
  @EntityGraph(attributePaths = {"destination"})
  Page<Tour> findAllByIsCustomFalse(Pageable pageable);

  @Query(
      "SELECT t FROM Tour t LEFT JOIN FETCH t.destination LEFT JOIN FETCH t.refundPolicy LEFT JOIN FETCH t.itineraries WHERE t.id = :id")
  Optional<Tour> findByIdWithDetails(@Param("id") UUID id);

  @EntityGraph(attributePaths = {"destination"})
  List<Tour> findTop10ByIsCustomFalseAndAverageRatingGreaterThanEqualOrderByAverageRatingDesc(
      Double minRating);
}
