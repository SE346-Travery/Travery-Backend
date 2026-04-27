package com.travery.traverybackend.dtos.response.vehicle;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
  private UUID id;
  private String licensePlate;
  private String vehicleType;
  private int totalSeats;
  private int floorCount;
  private String status;
}
