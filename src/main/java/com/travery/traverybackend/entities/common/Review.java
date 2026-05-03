package com.travery.traverybackend.entities.common;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.common.ReviewTargetType;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Review extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "booking_id", nullable = false)
  private UUID bookingId;

  @Enumerated(EnumType.STRING)
  @Column(name = "booking_type", nullable = false, length = 50)
  private com.travery.traverybackend.enums.booking.BookingType bookingType;

  @Column(name = "target_id", nullable = false)
  private UUID targetId;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false, length = 50)
  private ReviewTargetType targetType;

  @Column(name = "average_rating", nullable = false)
  private int averageRating;

  @Column(columnDefinition = "TEXT")
  private String content;
}
