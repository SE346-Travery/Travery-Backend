package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.SeatLayout;
import com.travery.traverybackend.enums.coach.CoachType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatLayoutRepository extends JpaRepository<SeatLayout, UUID> {

  @Query("SELECT sl FROM SeatLayout sl LEFT JOIN FETCH sl.items WHERE sl.coachType = :coachType")
  List<SeatLayout> findByCoachTypeWithItems(CoachType coachType);

  @Query("SELECT sl FROM SeatLayout sl LEFT JOIN FETCH sl.items")
  List<SeatLayout> findAllWithItems();

  @Query("SELECT sl FROM SeatLayout sl LEFT JOIN FETCH sl.items WHERE sl.id = :id")
  Optional<SeatLayout> findByIdWithItems(UUID id);
}
