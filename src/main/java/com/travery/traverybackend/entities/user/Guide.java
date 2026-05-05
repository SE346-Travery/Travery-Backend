package com.travery.traverybackend.entities.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "guides")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guide extends User {
  @Column(name = "guide_license", unique = true, nullable = false, length = 100)
  private String guideLicense;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "languages", columnDefinition = "jsonb")
  private List<String> languages;

  @Column(name = "employee_code", unique = true, nullable = false, length = 50)
  private String employeeCode;

  @Column(name = "years_experience")
  private int yearsExperience;

  @PrePersist
  protected void onCreate() {
    if (this.employeeCode == null) {
      this.employeeCode = "GUI-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
  }
}
