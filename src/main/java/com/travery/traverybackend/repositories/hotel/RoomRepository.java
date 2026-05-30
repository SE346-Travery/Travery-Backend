package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.entities.hotel.Room;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
  int countByRoomType_Id(UUID roomTypeId);
}
