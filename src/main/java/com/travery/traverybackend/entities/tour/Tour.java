package com.travery.traverybackend.entities.tour;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Indexed
public class Tour extends AbstractBaseEntity {

  @FullTextField(analyzer = "standard")
  @Column(nullable = false)
  private String name;

  @FullTextField(analyzer = "standard")
  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coordinator_id", nullable = false)
  private Coordinator coordinator;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id")
  private Hotel hotel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requested_by_user_id")
  private User requestedByUser;

  @IndexedEmbedded
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_id", nullable = false)
  private Destination destination;

  @Column(name = "pickup_location", nullable = false, length = 500)
  private String pickupLocation;

  @GenericField(sortable = Sortable.YES)
  @Column(name = "average_rating")
  @Builder.Default
  private Double averageRating = 0.0;

  @GenericField(sortable = Sortable.YES)
  @Column(name = "price_per_adult", nullable = false, precision = 12, scale = 2)
  private BigDecimal pricePerAdult;

  @Column(name = "price_per_child", nullable = false, precision = 12, scale = 2)
  private BigDecimal pricePerChild;

  @GenericField
  @Column(name = "is_custom", nullable = false)
  @Builder.Default
  private boolean isCustom = false;

  @Column(name = "min_participants")
  @Builder.Default
  private int minParticipants = 10;

  @Column(name = "max_participants")
  @Builder.Default
  private int maxParticipants = 30;

  @Column(name = "duration_days")
  @Builder.Default
  private int durationDays = 1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "refund_policy_id")
  private RefundPolicy refundPolicy;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TourItinerary> itineraries;

  @OneToMany(mappedBy = "tour")
  @IndexedEmbedded(structure = ObjectStructure.NESTED)
  private List<TourInstance> tourInstances;
}
