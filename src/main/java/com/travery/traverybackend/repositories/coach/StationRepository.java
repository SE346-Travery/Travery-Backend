package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.Station;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {
}
