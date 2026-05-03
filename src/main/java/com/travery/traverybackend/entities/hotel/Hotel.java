package com.travery.traverybackend.entities.hotel;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Hotel extends AbstractBaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(name = "star_rating", nullable = false)
  private int starRating;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, length = 500)
  private String address;

  @Column(name = "city_province", nullable = false, length = 100)
  private String cityProvince;

  @Column(nullable = false, precision = 10, scale = 8)
  private BigDecimal latitude;

  @Column(nullable = false, precision = 11, scale = 8)
  private BigDecimal longitude;

  @Column(name = "check_in_time")
  private LocalTime checkInTime;

  @Column(name = "check_out_time")
  private LocalTime checkOutTime;

  @Column(name = "refund_policy_id")
  private UUID refundPolicyId;

  @ManyToMany
  @JoinTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
  private Set<Amenity> amenities;
}
