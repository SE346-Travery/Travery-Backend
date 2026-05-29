package com.travery.traverybackend.repositories.coach;

import com.travery.traverybackend.entities.booking.CoachBookingSeat;
import com.travery.traverybackend.enums.booking.BookingStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachBookingSeatRepository extends JpaRepository<CoachBookingSeat, UUID> {

  @Query(
      "SELECT cbs FROM CoachBookingSeat cbs "
          + "WHERE cbs.coachBooking.coachTrip.id = :tripId "
          + "AND cbs.coachBooking.status NOT IN :excludedStatuses")
  List<CoachBookingSeat> findByTripIdAndBookingStatusNotIn(
      UUID tripId, List<BookingStatus> excludedStatuses);

  @Query(
      "SELECT COUNT(cbs) FROM CoachBookingSeat cbs "
          + "WHERE cbs.coachBooking.coachTrip.id = :tripId "
          + "AND cbs.coachBooking.status NOT IN :excludedStatuses")
  int countByTripIdAndBookingStatusNotIn(
      UUID tripId, List<BookingStatus> excludedStatuses);

  @Query(
      "SELECT COUNT(cbs) FROM CoachBookingSeat cbs WHERE cbs.coachBooking.coachTrip.id = :tripId AND cbs.coachBooking.status = :status")
  int countByCoachTripIdAndBookingStatus(UUID tripId, BookingStatus status);

  @Query(
      "SELECT cbs.coachBooking.coachTrip.id, COUNT(cbs) "
          + "FROM CoachBookingSeat cbs "
          + "WHERE cbs.coachBooking.coachTrip.id IN :tripIds "
          + "AND cbs.coachBooking.status NOT IN :excludedStatuses "
          + "GROUP BY cbs.coachBooking.coachTrip.id")
  List<Object[]> countBookedSeatsForTrips(
      @Param("tripIds") List<UUID> tripIds,
      @Param("excludedStatuses") List<BookingStatus> excludedStatuses);

  @Query(
      "SELECT cbs.coachBooking.id, COUNT(cbs) "
          + "FROM CoachBookingSeat cbs "
          + "WHERE cbs.coachBooking.id IN :bookingIds "
          + "GROUP BY cbs.coachBooking.id")
  List<Object[]> countSeatsByBookingIds(@Param("bookingIds") List<UUID> bookingIds);
}
