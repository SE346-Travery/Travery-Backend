package com.travery.traverybackend.dtos.request.hotel;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHotelRequest {
  private String name;

  private String description;

  private String address;

  private String cityProvince;

  private LocalTime checkInTime;

  private LocalTime checkOutTime;

  private List<UUID> amenityIds;

  private UUID refundPolicyId;
}
