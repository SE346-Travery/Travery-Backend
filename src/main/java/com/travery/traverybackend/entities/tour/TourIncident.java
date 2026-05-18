package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.tour.IncidentSeverity;
import com.travery.traverybackend.enums.tour.IncidentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tour_incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TourIncident extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_instance_id", nullable = false)
  private TourInstance tourInstance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", nullable = false)
  private User reporter;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private IncidentSeverity severity;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private IncidentStatus status = IncidentStatus.PENDING;
}
