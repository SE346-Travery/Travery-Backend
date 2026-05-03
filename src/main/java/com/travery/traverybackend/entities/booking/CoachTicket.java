package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.coach.CoachSeat;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coach_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CoachTicket extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coach_booking_id", nullable = false)
  private CoachBooking coachBooking;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coach_seat_id", nullable = false)
  private CoachSeat coachSeat;

  @Column(name = "passenger_name", length = 100)
  private String passengerName;

  @Column(name = "passenger_phone", length = 20)
  private String passengerPhone;

  @Column(name = "price_at_booking", nullable = false, precision = 12, scale = 2)
  private BigDecimal priceAtBooking;
}
