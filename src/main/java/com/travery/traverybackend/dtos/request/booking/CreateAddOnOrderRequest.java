package com.travery.traverybackend.dtos.request.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
public class CreateAddOnOrderRequest {
  @NotNull private UUID serviceId;

  @Min(1)
  private int quantity;

  @NotNull private LocalDateTime scheduledTime;
}
