package com.travery.traverybackend.entities.finance;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.PaymentMethod;
import com.travery.traverybackend.enums.finance.PaymentStatus;
import com.travery.traverybackend.enums.finance.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentTransaction extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "booking_id")
  private UUID bookingId;

  @Enumerated(EnumType.STRING)
  @Column(name = "booking_type", length = 50)
  private BookingType bookingType;

  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", length = 50)
  private PaymentMethod paymentMethod;

  @Column(name = "transaction_reference", length = 255)
  private String transactionReference;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false, length = 50)
  private TransactionType transactionType;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private PaymentStatus status;
}
