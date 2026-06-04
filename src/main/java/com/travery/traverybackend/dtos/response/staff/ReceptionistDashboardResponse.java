package com.travery.traverybackend.dtos.response.staff;

import java.util.List;
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
public class ReceptionistDashboardResponse {
  private long availableRooms;
  private long occupiedRooms;
  private long cleaningRooms;
  private long maintenanceRooms;
  private long todayCheckInCount;
  private long todayCheckOutCount;
  private List<DashboardGuestResponse> checkInQueue;
  private List<DashboardGuestResponse> checkOutQueue;
}
