package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.Coach;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepository extends JpaRepository<Coach, UUID> {

  @EntityGraph(attributePaths = "seatLayout")
  List<Coach> findAllByIsDeletedFalse();

  @EntityGraph(attributePaths = "seatLayout")
  Optional<Coach> findByIdAndIsDeletedFalse(UUID id);
}
