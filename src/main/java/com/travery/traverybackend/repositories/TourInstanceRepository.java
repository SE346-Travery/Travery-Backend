package com.travery.traverybackend.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;

public interface TourInstanceRepository extends JpaRepository<TourInstance, UUID> {
    List<TourInstance> findByTourIdAndStatusInAndStartDateGreaterThanEqualOrderByStartDateAsc(
            UUID tourId,
            List<TourInstanceStatus> statuses,
            LocalDate currentDate);
}
