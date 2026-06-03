package com.travery.traverybackend.entities.hotel;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Indexed
public class Hotel extends AbstractBaseEntity {

  @FullTextField(analyzer = "standard")
  @Column(nullable = false)
  private String name;

  @GenericField(sortable = Sortable.YES)
  @Column(name = "average_rating")
  @Builder.Default
  private Double averageRating = 0.0;

  @GenericField(sortable = Sortable.YES)
  @Column(name = "review_count")
  @Builder.Default
  private Integer reviewCount = 0;

  @FullTextField(analyzer = "standard")
  @Column(columnDefinition = "TEXT")
  private String description;

  @FullTextField(analyzer = "standard")
  @Column(nullable = false, length = 500)
  private String address;

  @FullTextField(analyzer = "standard")
  @Column(name = "city_province", nullable = false, length = 100)
  private String cityProvince;

  @Column(name = "check_in_time")
  private LocalTime checkInTime;

  @Column(name = "check_out_time")
  private LocalTime checkOutTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "refund_policy_id")
  private RefundPolicy refundPolicy;

  @ManyToMany
  @JoinTable(
      name = "hotel_amenities",
      joinColumns = @JoinColumn(name = "hotel_id"),
      inverseJoinColumns = @JoinColumn(name = "amenity_id"))
  @IndexedEmbedded
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private Set<Amenity> amenities;

  @OneToMany(mappedBy = "hotel")
  @IndexedEmbedded
  private List<RoomType> roomTypes;
}
