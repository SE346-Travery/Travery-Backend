package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.IdentityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tour_booking_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TourBookingMember extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_booking_id", nullable = false)
  private TourBooking tourBooking;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "identity_number")
  private String identityNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "identity_type")
  private IdentityType identityType;

  @Column(name = "is_child")
  private boolean isChild;

  private String status;
}
