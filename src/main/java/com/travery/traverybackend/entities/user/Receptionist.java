package com.travery.traverybackend.entities.user;

import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.enums.user.ShiftType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "receptionists")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receptionist extends User {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @Column(name = "employee_code", unique = true, nullable = false, length = 50)
  private String employeeCode;

  @Column(name = "shift_type", length = 50)
  @Enumerated(EnumType.STRING)
  private ShiftType shiftType;

  @PrePersist
  protected void onCreate() {
    if (this.employeeCode == null) {
      this.employeeCode =
          "REC-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
  }
}
