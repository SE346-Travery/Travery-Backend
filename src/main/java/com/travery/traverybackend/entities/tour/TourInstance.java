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

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

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

  @GenericField
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "current_participants")
  @Builder.Default
  private int currentParticipants = 0;

  @GenericField
  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  @Builder.Default
  private TourInstanceStatus status = TourInstanceStatus.PLANNING;
}
