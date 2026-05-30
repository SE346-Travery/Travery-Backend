package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.AddOnOrder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddOnOrderRepository extends JpaRepository<AddOnOrder, UUID> {
  List<AddOnOrder> findAllByHotelBooking_Id(UUID hotelBookingId);
}
