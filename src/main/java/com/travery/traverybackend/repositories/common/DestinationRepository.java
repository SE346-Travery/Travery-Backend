package com.travery.traverybackend.repositories.common;

import com.travery.traverybackend.entities.common.Destination;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {}
