package com.travery.traverybackend.entities.finance;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.finance.RefundStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "refund_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefundRequest extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_transaction_id", nullable = false)
  private PaymentTransaction paymentTransaction;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "processed_by_id")
  private Coordinator processedBy;

  @Column(name = "requested_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal requestedAmount;

  @Column(name = "actual_refunded", precision = 12, scale = 2)
  private BigDecimal actualRefunded;

  @Column(name = "customer_reason", columnDefinition = "TEXT")
  private String customerReason;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  @Builder.Default
  private RefundStatus status = RefundStatus.PENDING;
}
