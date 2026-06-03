package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.hotel.HotelService;
import com.travery.traverybackend.enums.booking.AddOnOrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "add_on_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AddOnOrder extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_booking_id", nullable = false)
  private HotelBooking hotelBooking;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_service_id", nullable = false)
  private HotelService hotelService;

  @Column(nullable = false)
  private int quantity;

  @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalPrice;

  @Column(name = "scheduled_time", nullable = false)
  private LocalDateTime scheduledTime;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  @Builder.Default
  private AddOnOrderStatus status = AddOnOrderStatus.PENDING;
}
