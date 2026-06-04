package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.enums.booking.BookingStatus;
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
public interface TourBookingRepository extends JpaRepository<TourBooking, UUID> {

  @Query(
      "SELECT tb FROM TourBooking tb JOIN FETCH tb.user JOIN FETCH tb.tourInstance ti JOIN FETCH ti.tour WHERE ti.id = :instanceId")
  List<TourBooking> findByTourInstanceId(@Param("instanceId") UUID instanceId);

  List<TourBooking> findByTourInstanceIdAndStatus(UUID instanceId, BookingStatus status);

  Optional<TourBooking> findByIdAndUser_Id(UUID id, UUID userId);

  @EntityGraph(attributePaths = {"tourInstance", "tourInstance.tour"})
  Page<TourBooking> findByUser_IdAndStatus(UUID userId, BookingStatus status, Pageable pageable);

  @EntityGraph(attributePaths = {"tourInstance", "tourInstance.tour"})
  Page<TourBooking> findByUser_Id(UUID userId, Pageable pageable);

  @Query(
      """
      SELECT b FROM TourBooking b
      JOIN FETCH b.tourInstance ti
      JOIN FETCH ti.tour t
      LEFT JOIN FETCH t.refundPolicy rp
      LEFT JOIN FETCH rp.rules
      WHERE b.id = :id
      """)
  Optional<TourBooking> findByIdWithDetails(@Param("id") UUID id);

  @Query("SELECT b FROM TourBooking b WHERE b.status = 'PENDING' AND b.paymentDeadline < :now")
  List<TourBooking> findExpiredPendingBookings(@Param("now") LocalDateTime now);
}
