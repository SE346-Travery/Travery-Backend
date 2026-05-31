package com.travery.traverybackend.entities.coach;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.coach.CoachType;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "seat_layouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SeatLayout extends AbstractBaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "coach_type", nullable = false, length = 50)
  private CoachType coachType;

  @Column(name = "total_seats", nullable = false)
  private int totalSeats;

  @OneToMany(mappedBy = "seatLayout", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SeatLayoutItem> items;
}
