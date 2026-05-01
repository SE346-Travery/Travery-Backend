package com.travery.traverybackend.entities.payment;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.tour.TourBooking;
import com.travery.traverybackend.enums.PaymentStatus;
import com.travery.traverybackend.enums.PaymentType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Payment extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_booking_id")
  private TourBooking tourBooking;

  // Note: These might be added later for Coach/Hotel bookings
  // private CoachBooking coachBooking;
  // private HotelBooking hotelBooking;

  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_type")
  private PaymentType paymentType;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Column(name = "vnpay_txn_id", unique = true)
  private String vnpayTxnId;

  @Column(name = "vnpay_response_code")
  private String vnpayResponseCode;
}
