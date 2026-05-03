package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.User;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Tour extends AbstractBaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coordinator_id", nullable = false)
  private Coordinator coordinator;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id")
  private Hotel hotel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requested_by_user_id")
  private User requestedByUser;

  @Column(name = "destination_code", nullable = false, length = 50)
  private String destinationCode;

  @Column(name = "pickup_location", nullable = false, length = 500)
  private String pickupLocation;

  @Column(name = "price_per_adult", nullable = false, precision = 12, scale = 2)
  private BigDecimal pricePerAdult;

  @Column(name = "price_per_child", nullable = false, precision = 12, scale = 2)
  private BigDecimal pricePerChild;

  @Column(name = "is_custom", nullable = false)
  @Builder.Default
  private boolean isCustom = false;

  @Column(name = "refund_policy_id")
  private UUID refundPolicyId;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TourItinerary> itineraries;
}
