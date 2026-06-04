package com.travery.traverybackend.entities.coach;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Route extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "origin_destination_id", nullable = false)
  private Destination originDestination;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_destination_id", nullable = false)
  private Destination destinationDestination;

  @Column(name = "distance_km", precision = 6, scale = 2)
  private BigDecimal distanceKm;

  @Column(name = "estimated_hours", precision = 4, scale = 1)
  private BigDecimal estimatedHours;

  @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal basePrice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "refund_policy_id")
  private RefundPolicy refundPolicy;

  @Column(name = "average_rating")
  @Builder.Default
  private Double averageRating = 0.0;

  @Column(name = "review_count")
  @Builder.Default
  private Integer reviewCount = 0;
}
