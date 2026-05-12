package com.travery.traverybackend.entities.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.enums.common.Region;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Destination extends AbstractBaseEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String code;

  @FullTextField(analyzer = "standard")
  @Column(nullable = false, length = 255)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Region region;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @Column(columnDefinition = "TEXT")
  private String description;

  @JsonIgnore
  @OneToMany(mappedBy = "destination")
  private List<Tour> tours = new ArrayList<>();
}
