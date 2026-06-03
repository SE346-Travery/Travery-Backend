package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.RoomAssignment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, UUID> {
  List<RoomAssignment> findAllByHotelBookingDetail_Id(UUID hotelBookingDetailId);

  List<RoomAssignment> findAllByHotelBookingDetail_HotelBooking_Id(UUID hotelBookingId);
}
