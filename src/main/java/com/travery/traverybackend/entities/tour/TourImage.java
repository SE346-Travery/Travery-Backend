package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tour_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TourImage extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_id", nullable = false)
  private Tour tour;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Column(name = "is_primary")
  private boolean isPrimary;
}
