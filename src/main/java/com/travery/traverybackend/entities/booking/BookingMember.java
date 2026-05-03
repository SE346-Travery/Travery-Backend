package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.booking.BookingType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "booking_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookingMember extends AbstractBaseEntity {

  @Column(name = "booking_id", nullable = false)
  private UUID bookingId;

  @Enumerated(EnumType.STRING)
  @Column(name = "booking_type", nullable = false, length = 50)
  private BookingType bookingType;

  @Column(name = "full_name", nullable = false, length = 100)
  private String fullName;

  @Column(name = "passport_number", nullable = false, length = 50)
  private String passportNumber;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;
}
