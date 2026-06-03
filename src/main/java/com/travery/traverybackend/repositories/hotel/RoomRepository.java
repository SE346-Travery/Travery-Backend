package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.entities.hotel.Room;
import com.travery.traverybackend.enums.hotel.RoomStatus;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
  int countByRoomType_Id(UUID roomTypeId);

  int countByRoomType_IdAndIsDeletedFalse(UUID roomTypeId);

  long countByHotel_IdAndStatus(UUID hotelId, RoomStatus status);

  @EntityGraph(attributePaths = "roomType")
  List<Room> findAllByRoomType_IdAndStatus(UUID roomTypeId, RoomStatus status);

  @EntityGraph(attributePaths = "roomType")
  List<Room> findAllByRoomType_IdAndHotel_IdAndStatus(UUID roomTypeId, UUID hotelId, RoomStatus status);

  @EntityGraph(attributePaths = "roomType")
  List<Room> findAllByHotel_Id(UUID hotelId);
}
