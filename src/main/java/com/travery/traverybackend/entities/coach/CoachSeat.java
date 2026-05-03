package com.travery.traverybackend.entities.coach;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.coach.SeatPosition;
import com.travery.traverybackend.enums.coach.SeatTier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coach_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CoachSeat extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coach_id", nullable = false)
  private Coach coach;

  @Column(name = "seat_name", nullable = false, length = 10)
  private String seatName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SeatTier tier;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SeatPosition position;
}
