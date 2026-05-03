package com.travery.traverybackend.entities.user;

import com.travery.traverybackend.enums.user.Department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coordinators")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinator extends User {
  @Column(name = "employee_code", unique = true, nullable = false, length = 50)
  private String employeeCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "department", length = 50)
  private Department department;

  @PrePersist
  protected void onCreate() {
    if (this.employeeCode == null) {
      this.employeeCode = "COO-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
  }
}
