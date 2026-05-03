package com.travery.traverybackend.entities.coach;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
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
  @JoinColumn(name = "origin_station_id", nullable = false)
  private Station originStation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_station_id", nullable = false)
  private Station destinationStation;

  @Column(name = "distance_km", precision = 6, scale = 2)
  private BigDecimal distanceKm;

  @Column(name = "estimated_hours", precision = 4, scale = 1)
  private BigDecimal estimatedHours;

  @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal basePrice;

  @Column(name = "refund_policy_id")
  private UUID refundPolicyId;
}
