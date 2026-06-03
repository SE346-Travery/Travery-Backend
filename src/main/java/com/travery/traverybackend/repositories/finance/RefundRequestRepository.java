package com.travery.traverybackend.repositories.finance;

import com.travery.traverybackend.entities.finance.RefundRequest;
import java.util.Optional;
import java.util.UUID;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, UUID> {
  @EntityGraph(attributePaths = {"user", "paymentTransaction"})
  @Query("SELECT r FROM RefundRequest r WHERE " +
         "(:status IS NULL OR r.status = :status) AND " +
         "(:bookingType IS NULL OR r.paymentTransaction.bookingType = :bookingType)")
  Page<RefundRequest> findByFilters(@Param("status") RefundStatus status, 
                                    @Param("bookingType") BookingType bookingType, 
                                    Pageable pageable);
  
  @EntityGraph(attributePaths = {"user", "paymentTransaction"})
  Optional<RefundRequest> findWithUserById(UUID id);
}
