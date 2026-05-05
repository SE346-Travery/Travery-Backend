package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.hotel.RoomType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "hotel_booking_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HotelBookingDetail extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_booking_id", nullable = false)
  private HotelBooking hotelBooking;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_type_id", nullable = false)
  private RoomType roomType;

  @Column(nullable = false)
  private int quantity;

  @Column(name = "price_at_booking", nullable = false, precision = 12, scale = 2)
  private BigDecimal priceAtBooking;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;
}
