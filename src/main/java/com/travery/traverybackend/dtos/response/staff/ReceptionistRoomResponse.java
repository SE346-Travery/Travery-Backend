package com.travery.traverybackend.dtos.response.staff;

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
public class ReceptionistRoomResponse {
  private UUID id;
  private String roomNumber;
  private String roomTypeName;
  private String status;
  private Integer floor;
}
