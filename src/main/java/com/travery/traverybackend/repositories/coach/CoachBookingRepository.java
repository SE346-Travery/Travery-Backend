package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.booking.CoachBooking;
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
public interface CoachBookingRepository extends JpaRepository<CoachBooking, UUID> {
  Optional<CoachBooking> findByIdAndUser_Id(UUID id, UUID userId);

  @EntityGraph(
      attributePaths = {
        "coachTrip",
        "coachTrip.route",
        "coachTrip.route.originDestination",
        "coachTrip.route.destinationDestination"
      })
  Page<CoachBooking> findByUser_Id(UUID userId, Pageable pageable);

  @EntityGraph(
      attributePaths = {
        "coachTrip",
        "coachTrip.route",
        "coachTrip.route.originDestination",
        "coachTrip.route.destinationDestination"
      })
  Page<CoachBooking> findByUser_IdAndStatus(UUID userId, BookingStatus status, Pageable pageable);

  @Query(
      "SELECT cb FROM CoachBooking cb "
          + "JOIN FETCH cb.coachTrip ct "
          + "JOIN FETCH ct.coach c "
          + "JOIN FETCH ct.route r "
          + "JOIN FETCH r.originDestination "
          + "JOIN FETCH r.destinationDestination "
          + "LEFT JOIN FETCH cb.bookedSeats bs "
          + "LEFT JOIN FETCH bs.seatLayoutItem "
          + "WHERE cb.id = :id")
  Optional<CoachBooking> findByIdWithDetails(@Param("id") UUID id);

  @Query("SELECT cb FROM CoachBooking cb WHERE cb.status = 'PENDING' AND cb.paymentDeadline < :now")
  List<CoachBooking> findExpiredPendingBookings(@Param("now") LocalDateTime now);

  int countByCoachTrip_Id(UUID tripId);

  List<CoachBooking> findByCoachTrip_Id(UUID tripId);
}
