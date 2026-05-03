package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.Driver;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import jakarta.persistence.*;
import java.time.LocalDate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tour_instances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TourInstance extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_id", nullable = false)
  private Tour tour;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coordinator_id", nullable = false)
  private Coordinator coordinator;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "guide_id")
  private Guide guide;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coach_id")
  private Coach coach;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "driver_id")
  private Driver driver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_booking_id")
  private HotelBooking hotelBooking;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "min_participants")
  @Builder.Default
  private int minParticipants = 10;

  @Column(name = "max_participants")
  @Builder.Default
  private int maxParticipants = 40;

  @Column(name = "current_participants")
  @Builder.Default
  private int currentParticipants = 0;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  @Builder.Default
  private TourInstanceStatus status = TourInstanceStatus.PLANNING;
}
