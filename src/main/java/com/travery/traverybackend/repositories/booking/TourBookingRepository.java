package com.travery.traverybackend.repositories.booking;

import com.travery.traverybackend.entities.booking.TourBooking;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourBookingRepository extends JpaRepository<TourBooking, UUID> {

  @Query("SELECT tb FROM TourBooking tb JOIN FETCH tb.user WHERE tb.tourInstance.id = :instanceId")
  List<TourBooking> findByTourInstanceId(@Param("instanceId") UUID instanceId);
}
