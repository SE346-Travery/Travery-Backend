package com.travery.traverybackend.dtos.response.staff;

import java.util.Map;
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
public class DashboardGuestResponse {
  private UUID bookingId;
  private String touristName;
  private String phoneNumber;
  private int memberCount;
  private int totalRooms;
  private Map<String, Integer> roomTypeBreakdown;
}
