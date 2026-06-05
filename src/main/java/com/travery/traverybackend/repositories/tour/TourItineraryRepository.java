package com.travery.traverybackend.repositories.tour;

import com.travery.traverybackend.entities.tour.TourItinerary;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourItineraryRepository extends JpaRepository<TourItinerary, UUID> {}
