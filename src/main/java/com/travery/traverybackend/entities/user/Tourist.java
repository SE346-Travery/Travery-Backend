package com.travery.traverybackend.entities.user;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import com.travery.traverybackend.enums.user.Gender;

@Entity
@Table(name = "tourists")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tourist extends User {
  @Column(name = "passport_number", unique = true, length = 50)
  private String passportNumber;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Enumerated(jakarta.persistence.EnumType.STRING)
  @Column(name = "gender", length = 10)
  private Gender gender;
}
