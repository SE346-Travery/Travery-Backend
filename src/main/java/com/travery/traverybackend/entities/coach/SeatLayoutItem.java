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
@Table(name = "seat_layout_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SeatLayoutItem extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_layout_id", nullable = false)
  private SeatLayout seatLayout;

  @Column(name = "seat_name", nullable = false, length = 10)
  private String seatName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SeatTier tier;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SeatPosition position;

  @Column(name = "row_number", nullable = false)
  private int rowNumber;

  @Column(name = "column_number", nullable = false)
  private int columnNumber;
}
