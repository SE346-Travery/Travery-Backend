package com.travery.traverybackend.repositories.finance;

import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.enums.booking.BookingType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

  Optional<PaymentTransaction> findByBookingIdAndBookingType(
      UUID bookingId, BookingType bookingType);
}
