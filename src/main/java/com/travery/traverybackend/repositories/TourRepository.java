package com.travery.traverybackend.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.travery.traverybackend.entities.tour.Tour;

import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TourRepository extends JpaRepository<Tour, UUID>, TourSearchCustomRepository {
    @EntityGraph(attributePaths = { "destination" })
    Page<Tour> findAllByIsCustomFalse(Pageable pageable);

    @EntityGraph(attributePaths = { "destination", "refundPolicy", "refundPolicy.rules", "itineraries" })
    @Query("SELECT t FROM Tour t WHERE t.id = :id")
    Optional<Tour> findByIdWithDetails(@Param("id") UUID id);

    List<Tour> findTop10ByIsCustomFalseAndAverageRatingGreaterThanEqualOrderByAverageRatingDesc(Double minRating);
}
