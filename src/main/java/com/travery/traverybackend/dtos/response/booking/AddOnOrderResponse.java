package com.travery.traverybackend.dtos.response.booking;

import java.math.BigDecimal;
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
public class AddOnOrderResponse {
  private UUID id;
  private String serviceName;
  private String category;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;
  private LocalDateTime scheduledTime;
  private String status;
}
