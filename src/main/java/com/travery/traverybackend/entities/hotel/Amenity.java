package com.travery.traverybackend.entities.hotel;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.hotel.AmenityType;
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
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Amenity extends AbstractBaseEntity {

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(name = "icon_url", length = 255)
  private String iconUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private AmenityType type;
}
