package com.travery.traverybackend.dtos.response.hotel;

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
public class HotelImageResponse {
  private UUID id;
  private String url;
  private boolean isThumbnail;
}
