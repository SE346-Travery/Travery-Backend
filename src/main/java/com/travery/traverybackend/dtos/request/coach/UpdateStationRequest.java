package com.travery.traverybackend.dtos.request.coach;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
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
public class UpdateStationRequest {

  @Size(max = 255, message = "Station name must not exceed 255 characters")
  private String name;

  @Size(max = 500, message = "Address must not exceed 500 characters")
  private String address;

  private UUID destinationId;

  @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
  @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
  @Digits(integer = 2, fraction = 8, message = "Latitude must fit numeric(10,8)")
  private BigDecimal latitude;

  @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
  @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
  @Digits(integer = 3, fraction = 8, message = "Longitude must fit numeric(11,8)")
  private BigDecimal longitude;
}
