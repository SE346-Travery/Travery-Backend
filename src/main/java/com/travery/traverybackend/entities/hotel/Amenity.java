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
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Amenity extends AbstractBaseEntity {

  @FullTextField(analyzer = "standard")
  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(name = "icon_url", length = 255)
  private String iconUrl;

  @Column(name = "icon_public_id", length = 255)
  private String iconPublicId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  @GenericField
  private AmenityType type;

  @Column(name = "is_active")
  @lombok.Builder.Default
  private boolean isActive = true;
}
