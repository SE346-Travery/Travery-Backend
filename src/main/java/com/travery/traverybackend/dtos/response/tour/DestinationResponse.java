package com.travery.traverybackend.dtos.response.tour;

import com.travery.traverybackend.enums.common.Region;
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
public class DestinationResponse {
  private UUID id;
  private String code;
  private String name;
  private Region region;
  private String imageUrl;
  private String description;
}
