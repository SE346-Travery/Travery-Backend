package com.travery.traverybackend.entities.hotel;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "TEXT")
  private String address;

  private String city;

  @Column(name = "location_code")
  private String locationCode;

  @Column(columnDefinition = "jsonb")
  private String amenities;
}
