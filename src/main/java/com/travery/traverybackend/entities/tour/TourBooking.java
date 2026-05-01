package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.enums.TourBookingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tour_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TourBooking extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private Tourist user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_instance_id", nullable = false)
  private TourInstance tourInstance;

  @Column(name = "passenger_name")
  private String passengerName;

  @Column(name = "passenger_phone")
  private String passengerPhone;

  @Column(name = "adult_count")
  private int adultCount;

  @Column(name = "child_count")
  private int childCount;

  @Column(name = "total_price")
  private BigDecimal totalPrice;

  @Column(name = "refund_amount")
  private BigDecimal refundAmount;

  @Column(name = "special_notes", columnDefinition = "TEXT")
  private String specialNotes;

  @Enumerated(EnumType.STRING)
  private TourBookingStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coordinator_id")
  private Coordinator coordinator;

  @Column(name = "coordinator_response", columnDefinition = "TEXT")
  private String coordinatorResponse;

  @Column(name = "cancellation_reason", columnDefinition = "TEXT")
  private String cancellationReason;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  @Column(name = "no_show_note", columnDefinition = "TEXT")
  private String noShowNote;

  @Column(name = "no_show_at")
  private LocalDateTime noShowAt;

  @OneToMany(mappedBy = "tourBooking", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TourBookingMember> members;
}
