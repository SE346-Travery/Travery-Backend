package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tour_itinerary_days")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TourItineraryDay extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_id", nullable = false)
  private Tour tour;

  @Column(name = "day_number")
  private int dayNumber;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "meal_count")
  private int mealCount;
}
