package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.AddOnOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddOnOrderRepository extends JpaRepository<AddOnOrder, UUID> {
  @EntityGraph(attributePaths = {"hotelService"})
  List<AddOnOrder> findAllByHotelBooking_Id(UUID hotelBookingId);

  @EntityGraph(attributePaths = {"hotelService"})
  @Query(
      "SELECT o FROM AddOnOrder o "
          + "WHERE o.hotelService.hotel.id = :hotelId "
          + "AND o.status = 'PENDING'")
  List<AddOnOrder> findActiveByHotelId(@Param("hotelId") UUID hotelId);

  @EntityGraph(attributePaths = {"hotelBooking"})
  Optional<AddOnOrder> findWithBookingById(UUID id);

  @EntityGraph(attributePaths = {"hotelService"})
  Optional<AddOnOrder> findWithServiceById(UUID id);
}
