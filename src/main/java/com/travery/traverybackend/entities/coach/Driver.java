package com.travery.traverybackend.entities.coach;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.coach.DriverStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

  @Column(name = "full_name", nullable = false, length = 100)
  private String fullName;

  @Column(name = "phone_number", nullable = false, unique = true, length = 20)
  private String phoneNumber;

  @Column(name = "license_number", nullable = false, unique = true, length = 50)
  private String licenseNumber;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private DriverStatus status = DriverStatus.AVAILABLE;
}
