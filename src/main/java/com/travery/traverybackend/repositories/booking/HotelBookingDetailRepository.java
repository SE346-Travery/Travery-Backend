package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.HotelBookingDetail;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingDetailRepository extends JpaRepository<HotelBookingDetail, UUID> {
  List<HotelBookingDetail> findAllByHotelBooking_Id(UUID hotelBookingId);

  @EntityGraph(attributePaths = {"roomType", "roomType.hotel"})
  List<HotelBookingDetail> findAllWithRoomTypeAndHotelByHotelBooking_Id(UUID hotelBookingId);

  @Query(
      "SELECT DISTINCT d.hotelBooking.id, d.roomType.hotel.name "
          + "FROM HotelBookingDetail d WHERE d.hotelBooking.id IN :bookingIds")
  List<Object[]> findHotelNamesByBookingIds(@Param("bookingIds") List<UUID> bookingIds);

  @Query(
      "SELECT COUNT(d) FROM HotelBookingDetail d "
          + "WHERE d.roomType.hotel.id = :hotelId AND d.hotelBooking.startDate = :date "
          + "AND d.hotelBooking.status = 'PAID'")
  long countTodayCheckIns(@Param("hotelId") UUID hotelId, @Param("date") LocalDate date);

  @Query(
      "SELECT COUNT(d) FROM HotelBookingDetail d "
          + "WHERE d.roomType.hotel.id = :hotelId AND d.hotelBooking.endDate = :date "
          + "AND d.hotelBooking.status = 'CHECKED_IN'")
  long countTodayCheckOuts(@Param("hotelId") UUID hotelId, @Param("date") LocalDate date);

  @Query(
      "SELECT SUM(d.quantity) FROM HotelBookingDetail d "
          + "WHERE d.roomType.id = :roomTypeId "
          + "AND d.hotelBooking.status IN (:statuses) "
          + "AND NOT (d.hotelBooking.endDate <= :startDate OR d.hotelBooking.startDate >= :endDate)")
  Integer sumBookedQuantity(
      @Param("roomTypeId") UUID roomTypeId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("statuses") List<BookingStatus> statuses);
}
