package com.travery.traverybackend.repositories.common;

import com.travery.traverybackend.entities.common.Review;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.common.ReviewTargetType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    boolean existsByBookingIdAndBookingType(UUID bookingId, BookingType bookingType);

    @EntityGraph(attributePaths = "user")
    Page<Review> findByTargetIdAndTargetType(
            UUID targetId, ReviewTargetType targetType, Pageable pageable);

    @Query("SELECT COALESCE(AVG(r.averageRating), 0) FROM Review r "
            + "WHERE r.targetId = :targetId AND r.targetType = :targetType")
    double getAverageRating(
            @Param("targetId") UUID targetId, @Param("targetType") ReviewTargetType targetType);
}
