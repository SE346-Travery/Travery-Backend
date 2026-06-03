package com.travery.traverybackend.dtos.request.hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateHotelRequest {
  @NotBlank
  private String name;

  private String description;

  @NotBlank
  private String address;

  @NotBlank
  private String cityProvince;

  @NotNull
  private LocalTime checkInTime;

  @NotNull
  private LocalTime checkOutTime;

  private List<UUID> amenityIds;

  @NotNull
  private UUID refundPolicyId;
}
