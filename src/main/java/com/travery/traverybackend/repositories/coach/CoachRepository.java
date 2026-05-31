package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.Coach;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepository extends JpaRepository<Coach, UUID> {}
