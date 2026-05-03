package com.travery.traverybackend.entities.hotel;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.hotel.BedType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RoomType extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal basePrice;

  @Column(name = "capacity_adults", nullable = false)
  private int capacityAdults;

  @Column(name = "capacity_children")
  @Builder.Default
  private int capacityChildren = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "bed_type", nullable = false, length = 50)
  private BedType bedType;

  @ManyToMany
  @JoinTable(name = "room_type_amenities", joinColumns = @JoinColumn(name = "room_type_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
  private Set<Amenity> amenities;
}
