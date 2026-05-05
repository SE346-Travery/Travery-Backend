package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.BookingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coach_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CoachBooking extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coach_trip_id", nullable = false)
  private CoachTrip coachTrip;

  @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalPrice;

  @Column(name = "payment_deadline")
  private LocalDateTime paymentDeadline;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  @Builder.Default
  private BookingStatus status = BookingStatus.PENDING;
}
