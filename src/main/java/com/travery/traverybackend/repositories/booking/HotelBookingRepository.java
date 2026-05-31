package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.HotelBooking;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, UUID> {}
