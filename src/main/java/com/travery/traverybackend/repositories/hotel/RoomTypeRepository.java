package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.entities.hotel.RoomType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {
  List<RoomType> findAllByHotel_Id(UUID hotelId);
}
