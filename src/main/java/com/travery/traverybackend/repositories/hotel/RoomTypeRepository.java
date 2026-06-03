package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.entities.hotel.RoomType;
import java.util.List;
import java.util.UUID;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {
  @EntityGraph(attributePaths = "amenities")
  List<RoomType> findAllByHotel_Id(UUID hotelId);

  List<RoomType> findAllByHotel_IdIn(List<UUID> hotelIds);

  interface HotelMinPriceProjection {
    UUID getHotelId();
    BigDecimal getMinPrice();
  }

  @Query("SELECT r.hotel.id as hotelId, MIN(r.basePrice) as minPrice " +
         "FROM RoomType r " +
         "WHERE r.hotel.id IN :hotelIds AND r.isDeleted = false " +
         "GROUP BY r.hotel.id")
  List<HotelMinPriceProjection> findMinPricesByHotelIds(@Param("hotelIds") List<UUID> hotelIds);

  @Query("""
      SELECT DISTINCT rt.hotel.id 
      FROM RoomType rt 
      WHERE rt.isDeleted = false 
      AND (
          SELECT COUNT(r.id) FROM Room r WHERE r.roomType.id = rt.id AND r.isDeleted = false
      ) - COALESCE((
          SELECT SUM(hbd.quantity) FROM HotelBookingDetail hbd 
          JOIN hbd.hotelBooking hb 
          WHERE hbd.roomType.id = rt.id 
          AND hb.status IN ('PENDING', 'PAID', 'CHECKED_IN') 
          AND hb.startDate < :endDate 
          AND hb.endDate > :startDate
      ), 0) >= :roomCount
  """)
  List<UUID> findAvailableHotelIds(
      @Param("startDate") java.time.LocalDate startDate, 
      @Param("endDate") java.time.LocalDate endDate, 
      @Param("roomCount") int roomCount);
}
