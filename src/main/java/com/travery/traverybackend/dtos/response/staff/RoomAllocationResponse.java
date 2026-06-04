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
public class RoomAllocationResponse {
  private String roomTypeName;
  private int quantity;
  private List<String> assignedRoomNumbers;
}
