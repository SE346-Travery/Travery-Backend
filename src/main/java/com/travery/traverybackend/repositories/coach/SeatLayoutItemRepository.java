package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatLayoutItemRepository extends JpaRepository<SeatLayoutItem, UUID> {}
