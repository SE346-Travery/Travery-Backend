package com.travery.traverybackend.entities.coach;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Station extends AbstractBaseEntity {

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false, length = 500)
  private String address;

  @Column(name = "city_province", nullable = false, length = 100)
  private String cityProvince;

  @Column(precision = 10, scale = 8)
  private BigDecimal latitude;

  @Column(precision = 11, scale = 8)
  private BigDecimal longitude;
}
