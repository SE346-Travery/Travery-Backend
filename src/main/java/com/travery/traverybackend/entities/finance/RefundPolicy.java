package com.travery.traverybackend.entities.finance;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.finance.RefundServiceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
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

  @OneToMany(mappedBy = "refundPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RefundPolicyRule> rules;

  @Column(name = "is_deleted", nullable = false)
  @lombok.Builder.Default
  private boolean isDeleted = false;
}
