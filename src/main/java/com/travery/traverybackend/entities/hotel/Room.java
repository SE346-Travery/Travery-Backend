package com.travery.traverybackend.entities.hotel;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.hotel.RoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "rooms",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"hotel_id", "room_number"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Room extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_type_id", nullable = false)
  private RoomType roomType;

  @Column(name = "room_number", nullable = false, length = 50)
  private String roomNumber;

  @Column(nullable = false)
  private int floor;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private RoomStatus status = RoomStatus.AVAILABLE;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDeleted = false;
}
