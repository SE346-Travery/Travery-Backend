package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.Driver;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {

  List<Driver> findAllByIsDeletedFalse();

  Optional<Driver> findByIdAndIsDeletedFalse(UUID id);
}
