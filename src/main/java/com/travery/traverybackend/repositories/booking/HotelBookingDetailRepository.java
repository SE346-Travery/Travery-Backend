package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.HotelBookingDetail;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingDetailRepository extends JpaRepository<HotelBookingDetail, UUID> {
  List<HotelBookingDetail> findAllByHotelBooking_Id(UUID hotelBookingId);

  @Query(
      "SELECT COUNT(d) FROM HotelBookingDetail d "
          + "WHERE d.roomType.hotel.id = :hotelId AND d.startDate = :date "
          + "AND d.hotelBooking.status = 'PAID'")
  long countTodayCheckIns(@Param("hotelId") UUID hotelId, @Param("date") LocalDate date);

  @Query(
      "SELECT COUNT(d) FROM HotelBookingDetail d "
          + "WHERE d.roomType.hotel.id = :hotelId AND d.endDate = :date "
          + "AND d.hotelBooking.status = 'CHECKED_IN'")
  long countTodayCheckOuts(@Param("hotelId") UUID hotelId, @Param("date") LocalDate date);

  @Query(
      "SELECT SUM(d.quantity) FROM HotelBookingDetail d "
          + "WHERE d.roomType.id = :roomTypeId "
          + "AND d.hotelBooking.status IN (:statuses) "
          + "AND NOT (d.endDate <= :startDate OR d.startDate >= :endDate)")
  Integer sumBookedQuantity(
      @Param("roomTypeId") UUID roomTypeId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("statuses") List<BookingStatus> statuses);
}
