package com.travery.traverybackend.entities.finance;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.finance.RefundTimeUnit;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

  @Column(name = "time_before", nullable = false)
  private int timeBefore;

  @Enumerated(EnumType.STRING)
  @Column(name = "time_unit", nullable = false, length = 10)
  @Builder.Default
  private RefundTimeUnit timeUnit = RefundTimeUnit.DAYS;

  @Column(name = "refund_percentage", nullable = false, precision = 5, scale = 2)
  private BigDecimal refundPercentage;
}
