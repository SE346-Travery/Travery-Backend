package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, UUID> {
  Page<HotelBooking> findAllByUser_IdAndStatus(
      UUID userId, BookingStatus status, Pageable pageable);

  Page<HotelBooking> findAllByUser_Id(UUID userId, Pageable pageable);

  @Query("SELECT b FROM HotelBooking b WHERE b.status = 'PENDING' AND b.paymentDeadline < :now")
  List<HotelBooking> findExpiredPendingBookings(@Param("now") LocalDateTime now);
}
