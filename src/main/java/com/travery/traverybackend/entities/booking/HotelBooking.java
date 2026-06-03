package com.travery.traverybackend.entities.booking;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.BookingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "hotel_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HotelBooking extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_instance_id")
  private TourInstance tourInstance;

  @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalPrice;

  @Column(name = "payment_deadline")
  private LocalDateTime paymentDeadline;

  @Column(name = "contact_name", nullable = false)
  private String contactName;

  @Column(name = "contact_phone", nullable = false, length = 20)
  private String contactPhone;

  @Column(name = "special_requests", columnDefinition = "TEXT")
  private String specialRequests;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "actual_check_in_time")
  private LocalDateTime actualCheckInTime;

  @Column(name = "actual_check_out_time")
  private LocalDateTime actualCheckOutTime;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  @Builder.Default
  private BookingStatus status = BookingStatus.PENDING;
}
