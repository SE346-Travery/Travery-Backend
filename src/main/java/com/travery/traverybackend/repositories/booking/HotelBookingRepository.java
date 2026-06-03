package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, UUID> {
  Page<HotelBooking> findAllByUser_IdAndStatus(
      UUID userId, BookingStatus status, Pageable pageable);

  @EntityGraph(attributePaths = {"user"})
  Optional<HotelBooking> findByIdAndUser_Id(UUID id, UUID userId);

  Page<HotelBooking> findAllByUser_Id(UUID userId, Pageable pageable);

  @Query("SELECT b FROM HotelBooking b WHERE b.status = 'PENDING' AND b.paymentDeadline < :now")
  List<HotelBooking> findExpiredPendingBookings(@Param("now") LocalDateTime now);

  @Query(
      "SELECT DISTINCT b FROM HotelBooking b JOIN HotelBookingDetail d ON d.hotelBooking.id = b.id "
          + "WHERE d.roomType.hotel.id = :hotelId "
          + "AND (:date IS NULL OR b.startDate = :date OR b.endDate = :date) "
          + "AND (:guestName IS NULL OR LOWER(b.user.fullName) LIKE LOWER(CONCAT('%', :guestName, '%'))) "
          + "AND (:status IS NULL OR b.status = :status)")
  Page<HotelBooking> findReceptionistQueue(
      @Param("hotelId") UUID hotelId,
      @Param("date") LocalDate date,
      @Param("guestName") String guestName,
      @Param("status") BookingStatus status,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT b FROM HotelBooking b JOIN HotelBookingDetail d ON d.hotelBooking.id = b.id "
          + "WHERE d.roomType.hotel.id = :hotelId "
          + "AND b.startDate = :date "
          + "AND b.status = 'PAID'")
  List<HotelBooking> findTodayCheckInBookings(
      @Param("hotelId") UUID hotelId, @Param("date") LocalDate date);

  @Query(
      "SELECT DISTINCT b FROM HotelBooking b JOIN HotelBookingDetail d ON d.hotelBooking.id = b.id "
          + "WHERE d.roomType.hotel.id = :hotelId "
          + "AND b.endDate = :date "
          + "AND b.status = 'CHECKED_IN'")
  List<HotelBooking> findTodayCheckOutBookings(
      @Param("hotelId") UUID hotelId, @Param("date") LocalDate date);
}
