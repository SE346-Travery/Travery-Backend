package com.travery.traverybackend.entities.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.coach.Station;
import com.travery.traverybackend.entities.tour.Tour;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Indexed
public class Destination extends AbstractBaseEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String code;

  @FullTextField(analyzer = "standard")
  @Column(nullable = false, length = 255)
  private String name;

  @JsonIgnore
  @OneToMany(mappedBy = "destination")
  private List<Tour> tours = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
  private List<Station> stations = new ArrayList<>();
}
