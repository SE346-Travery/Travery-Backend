package com.travery.traverybackend.repositories.user;

import com.travery.traverybackend.entities.user.Receptionist;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist, UUID> {
  Optional<Receptionist> findById(UUID id);

  List<Receptionist> findByHotelId(UUID hotelId);
}
