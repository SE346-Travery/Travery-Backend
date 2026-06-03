package com.travery.traverybackend.dtos.response.common;

import java.time.LocalDateTime;
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
public class ReviewResponse {
  private UUID id;
  private int rating;
  private String content;
  private String reviewerName;
  private LocalDateTime createdAt;
}
