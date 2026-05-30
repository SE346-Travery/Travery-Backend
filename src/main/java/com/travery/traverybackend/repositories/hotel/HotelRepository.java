package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.entities.hotel.Hotel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID>, HotelSearchCustomRepository {}
