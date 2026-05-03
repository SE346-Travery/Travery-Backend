package com.travery.traverybackend.entities.finance;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "refund_policy_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefundPolicyRule extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "refund_policy_id", nullable = false)
  private RefundPolicy refundPolicy;

  @Column(name = "days_before", nullable = false)
  private int daysBefore;

  @Column(name = "refund_percentage", nullable = false, precision = 5, scale = 2)
  private BigDecimal refundPercentage;
}
