package com.travery.traverybackend.repositories.tour;

import com.travery.traverybackend.entities.tour.TourItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TourItineraryRepository extends JpaRepository<TourItinerary, UUID> {
}
