package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.BookingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

  @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal basePrice;

  @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalPrice;

  @Column(name = "payment_deadline")
  private LocalDateTime paymentDeadline;

  @Column(name = "contact_name", length = 100)
  private String contactName;

  @Column(name = "contact_phone", length = 20)
  private String contactPhone;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  @Builder.Default
  private BookingStatus status = BookingStatus.PENDING;

  @OneToMany(mappedBy = "coachBooking", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CoachBookingSeat> bookedSeats;
}
