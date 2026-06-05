package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.booking.AttendanceStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.booking.MemberType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

@Entity
@Table(name = "booking_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Indexed
public class BookingMember extends AbstractBaseEntity {

  @Column(name = "booking_id", nullable = false)
  @KeywordField
  private UUID bookingId;

  @Enumerated(EnumType.STRING)
  @Column(name = "booking_type", nullable = false, length = 50)
  @KeywordField
  private BookingType bookingType;

  @Column(name = "full_name", nullable = false, length = 100)
  @FullTextField
  private String fullName;

  @Column(name = "identity_number", nullable = false, length = 50)
  @FullTextField
  @KeywordField(name = "identityNumber_keyword")
  private String identityNumber;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Enumerated(EnumType.STRING)
  @Column(name = "attendance_status", nullable = false, length = 50)
  @Builder.Default
  private AttendanceStatus attendanceStatus = AttendanceStatus.NOT_CHECKED;

  @Enumerated(EnumType.STRING)
  @Column(name = "member_type", nullable = false, length = 20)
  private MemberType memberType;
}
