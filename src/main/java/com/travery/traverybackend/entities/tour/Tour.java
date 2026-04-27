package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.TourStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
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

  @Column(columnDefinition = "jsonb")
  private String description;

  @Column(name = "price_per_adult")
  private BigDecimal pricePerAdult;

  @Column(name = "price_per_child")
  private BigDecimal pricePerChild;

  @Column(name = "max_capacity")
  private int maxCapacity;

  @Column(name = "min_capacity")
  private int minCapacity;

  @Column(name = "is_custom")
  private boolean isCustom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_for_user_id")
  private User createdForUser;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Enumerated(EnumType.STRING)
  private TourStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id")
  private Hotel hotel;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TourItineraryDay> itinerary;
}
