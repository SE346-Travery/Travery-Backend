package com.travery.traverybackend.dtos.response.tour;

import com.travery.traverybackend.enums.tour.IncidentSeverity;
import com.travery.traverybackend.enums.tour.IncidentStatus;
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
public class TourIncidentResponse {
  private UUID id;
  private UUID tourInstanceId;
  private UUID reporterId;
  private String reporterName;
  private String title;
  private String description;
  private IncidentSeverity severity;
  private IncidentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
