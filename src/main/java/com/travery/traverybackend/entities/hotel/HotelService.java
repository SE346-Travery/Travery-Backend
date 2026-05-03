package com.travery.traverybackend.entities.hotel;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.hotel.ServiceCategory;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "hotel_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HotelService extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private ServiceCategory category;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @Column(nullable = false, length = 50)
  private String unit;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "is_active")
  @Builder.Default
  private boolean isActive = true;
}
