package com.travery.traverybackend.entities.coach;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coach_trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CoachTrip extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coach_id", nullable = false)
  private Coach coach;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "driver_id", nullable = false)
  private Driver driver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coordinator_id", nullable = false)
  private Coordinator coordinator;

  @Column(name = "departure_time", nullable = false)
  private LocalDateTime departureTime;

  @Column(name = "arrival_time")
  private LocalDateTime arrivalTime;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  @Builder.Default
  private CoachTripStatus status = CoachTripStatus.OPEN;
}
