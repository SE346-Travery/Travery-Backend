package com.travery.traverybackend.entities.coach;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.coach.CoachStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coaches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Coach extends AbstractBaseEntity {

  @Column(name = "license_plate", nullable = false, unique = true, length = 20)
  private String licensePlate;

  @Enumerated(EnumType.STRING)
  @Column(name = "coach_type", nullable = false, length = 50)
  private CoachType coachType;

  @Column(nullable = false)
  private int capacity;

  @Column(name = "is_deleted", nullable = false)
  @Builder.Default
  private boolean isDeleted = false;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private CoachStatus status = CoachStatus.ACTIVE;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_layout_id")
  private SeatLayout seatLayout;
}
