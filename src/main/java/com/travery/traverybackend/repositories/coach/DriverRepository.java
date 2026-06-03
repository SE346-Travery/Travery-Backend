package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.coach.Driver;
import com.travery.traverybackend.enums.coach.DriverStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
  List<Driver> findByStatusNot(DriverStatus status);

  Optional<Driver> findByPhoneNumber(String phoneNumber);

  Optional<Driver> findByLicenseNumber(String licenseNumber);
}

