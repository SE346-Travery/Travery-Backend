package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, UUID> {
    Page<HotelBooking> findAllByUser_IdAndStatus(
            UUID userId, BookingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = { "user" })
    Optional<HotelBooking> findByIdAndUser_Id(UUID id, UUID userId);

    Page<HotelBooking> findAllByUser_Id(UUID userId, Pageable pageable);

    @Query("SELECT b FROM HotelBooking b WHERE b.status = 'PENDING' AND b.paymentDeadline < :now")
    List<HotelBooking> findExpiredPendingBookings(@Param("now") LocalDateTime now);

    @Query(value = "SELECT DISTINCT hb.* FROM hotel_bookings hb "
            + "JOIN hotel_booking_details hbd ON hbd.hotel_booking_id = hb.id "
            + "JOIN room_types rt ON rt.id = hbd.room_type_id "
            + "JOIN users u ON u.id = hb.user_id "
            + "WHERE rt.hotel_id = :hotelId "
            + "AND (:date IS NULL OR hb.start_date = :date OR hb.end_date = :date) "
            + "AND (:guestName IS NULL OR lower(u.full_name) LIKE lower(CONCAT('%', CAST(:guestName AS text), '%'))) "
            + "AND (:status IS NULL OR hb.status = CAST(:status AS text))", countQuery = "SELECT COUNT(DISTINCT hb.id) FROM hotel_bookings hb "
                    + "JOIN hotel_booking_details hbd ON hbd.hotel_booking_id = hb.id "
                    + "JOIN room_types rt ON rt.id = hbd.room_type_id "
                    + "JOIN users u ON u.id = hb.user_id "
                    + "WHERE rt.hotel_id = :hotelId "
                    + "AND (:date IS NULL OR hb.start_date = :date OR hb.end_date = :date) "
                    + "AND (:guestName IS NULL OR lower(u.full_name) LIKE lower(CONCAT('%', CAST(:guestName AS text), '%'))) "
                    + "AND (:status IS NULL OR hb.status = CAST(:status AS text))", nativeQuery = true)
    Page<HotelBooking> findReceptionistQueue(
            @Param("hotelId") UUID hotelId,
            @Param("date") LocalDate date,
            @Param("guestName") String guestName,
            @Param("status") String status,
            Pageable pageable);

    @Query("SELECT DISTINCT b FROM HotelBooking b JOIN HotelBookingDetail d ON d.hotelBooking.id = b.id "
            + "WHERE d.roomType.hotel.id = :hotelId "
            + "AND b.startDate = :date "
            + "AND b.status = 'PAID'")
    List<HotelBooking> findTodayCheckInBookings(
            @Param("hotelId") UUID hotelId, @Param("date") LocalDate date);

    @Query("SELECT DISTINCT b FROM HotelBooking b JOIN HotelBookingDetail d ON d.hotelBooking.id = b.id "
            + "WHERE d.roomType.hotel.id = :hotelId "
            + "AND b.endDate = :date "
            + "AND b.status = 'CHECKED_IN'")
    List<HotelBooking> findTodayCheckOutBookings(
            @Param("hotelId") UUID hotelId, @Param("date") LocalDate date);
}
