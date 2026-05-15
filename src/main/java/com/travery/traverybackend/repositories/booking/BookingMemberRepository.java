package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.enums.booking.BookingType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingMemberRepository extends JpaRepository<BookingMember, UUID> {

  List<BookingMember> findAllByBookingIdAndBookingType(UUID bookingId, BookingType bookingType);

  int countByBookingIdAndBookingType(UUID bookingId, BookingType bookingType);

  /**
   * Batch fetch member counts for multiple bookings in a single query.
   * Returns List of [bookingId, count] pairs.
   */
  @Query(
      "SELECT bm.bookingId, COUNT(bm) FROM BookingMember bm "
          + "WHERE bm.bookingId IN :bookingIds AND bm.bookingType = :bookingType "
          + "GROUP BY bm.bookingId")
  List<Object[]> countByBookingIds(
      @Param("bookingIds") List<UUID> bookingIds, @Param("bookingType") BookingType bookingType);
}
