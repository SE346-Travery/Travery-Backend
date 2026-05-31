package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.entities.hotel.Amenity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {
  boolean existsByName(String name);
}
