package com.travery.traverybackend.dtos.request.hotel;

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
public class UpdateRoomRequest {
  private String roomNumber;

  private Integer floor;

  private UUID roomTypeId;
}
