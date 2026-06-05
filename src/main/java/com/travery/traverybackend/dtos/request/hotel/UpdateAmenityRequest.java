package com.travery.traverybackend.dtos.request.hotel;

import com.travery.traverybackend.enums.hotel.AmenityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAmenityRequest {
  private String name;

  private AmenityType type;

  private MultipartFile iconImage;
}
