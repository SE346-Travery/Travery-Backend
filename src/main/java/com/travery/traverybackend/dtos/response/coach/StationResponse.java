package com.travery.traverybackend.dtos.response.coach;

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
public class StationResponse {
  private UUID id;
  private String name;
  private String address;
  private UUID destinationId;
  private String destinationName;
}
