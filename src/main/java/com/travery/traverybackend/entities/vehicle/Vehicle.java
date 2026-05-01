package com.travery.traverybackend.entities.vehicle;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Vehicle extends AbstractBaseEntity {

  @Column(name = "license_plate", nullable = false, unique = true)
  private String licensePlate;

  @Column(name = "vehicle_type")
  private String vehicleType;

  @Column(name = "total_seats")
  private int totalSeats;

  @Column(name = "floor_count")
  private int floorCount;

  @Enumerated(EnumType.STRING)
  private VehicleStatus status;
}
