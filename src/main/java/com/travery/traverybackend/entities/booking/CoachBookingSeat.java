package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "coach_booking_seats",
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"coach_booking_id", "seat_layout_item_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CoachBookingSeat extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coach_booking_id", nullable = false)
  private CoachBooking coachBooking;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_layout_item_id", nullable = false)
  private SeatLayoutItem seatLayoutItem;
}
