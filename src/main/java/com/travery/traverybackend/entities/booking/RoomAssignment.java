package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.hotel.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "room_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RoomAssignment extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_booking_detail_id", nullable = false)
  private HotelBookingDetail hotelBookingDetail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;
}
