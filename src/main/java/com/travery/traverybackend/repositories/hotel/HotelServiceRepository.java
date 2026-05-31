package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.entities.hotel.HotelService;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelServiceRepository extends JpaRepository<HotelService, UUID> {
  List<HotelService> findAllByHotel_Id(UUID hotelId);
}
