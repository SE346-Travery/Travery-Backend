package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.coach.SeatPosition;
import com.travery.traverybackend.enums.coach.SeatTier;
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
public class SeatLayoutItemResponse {
  private UUID id;
  private String seatName;
  private SeatTier tier;
  private SeatPosition position;
  private int rowNumber;
  private int columnNumber;
}
