package com.travery.traverybackend.entities.finance;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.finance.RefundServiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "refund_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefundPolicy extends AbstractBaseEntity {

  @Column(nullable = false, length = 255)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "service_type", nullable = false, length = 50)
  private RefundServiceType serviceType;
}
