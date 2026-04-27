package com.travery.traverybackend.entities.vehicle;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Driver extends AbstractBaseEntity {

  @Column(name = "full_name", nullable = false)
  private String fullName;

  private String phone;

  @Column(name = "license_number")
  private String licenseNumber;

  @Enumerated(EnumType.STRING)
  private DriverStatus status;
}
