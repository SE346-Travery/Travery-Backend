package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.BookingMember;
import com.travery.traverybackend.enums.booking.BookingType;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingMemberRepository extends JpaRepository<BookingMember, UUID> {
  List<BookingMember> findByBookingIdInAndBookingType(
      Collection<UUID> bookingIds, BookingType bookingType);
}
