package com.travery.traverybackend.dtos.response.tour;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourSummaryResponse implements Serializable {
  private UUID id;
  private String name;
  private Double averageRating;
  private BigDecimal price;
  private String thumbnailUrl;
  private String destinationName;
  private Integer durationDays;
}
