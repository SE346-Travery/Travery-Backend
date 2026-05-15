package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.PaymentStatus;
import com.travery.traverybackend.enums.finance.TransactionType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.finance.PaymentTransactionRepository;
import com.travery.traverybackend.services.booking.PaymentService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  private static final String STUB_PAYMENT_BASE_URL = "https://payment.stub.travery.com/pay/";

  private final TourBookingRepository tourBookingRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;

  @Override
  @Transactional
  public PaymentInitiationResponse initiatePayment(
      UUID bookingId, InitiatePaymentRequest request, UUID userId) {

    // 1. Load booking and verify ownership
    TourBooking booking =
        tourBookingRepository
            .findByIdAndUser_Id(bookingId, userId)
            .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));

    // 2. Validate booking is PENDING
    if (booking.getStatus() != BookingStatus.PENDING) {
      throw new BaseAppException(BookingErrorCode.BOOKING_NOT_PENDING);
    }

    // 3. Validate payment deadline has not passed
    if (booking.getPaymentDeadline().isBefore(LocalDateTime.now())) {
      throw new BaseAppException(BookingErrorCode.PAYMENT_DEADLINE_EXPIRED);
    }

    // 4. Create PaymentTransaction (polymorphic: bookingId + bookingType)
    PaymentTransaction transaction =
        PaymentTransaction.builder()
            .user(booking.getUser())
            .bookingId(booking.getId())
            .bookingType(BookingType.TOUR_BOOKING)
            .amount(booking.getTotalPrice())
            .paymentMethod(request.getPaymentMethod())
            .transactionType(TransactionType.PAYMENT)
            .status(PaymentStatus.PENDING)
            .build();
    transaction = paymentTransactionRepository.save(transaction);

    // 5. Generate stub payment URL (replace with real VNPay/MoMo SDK later)
    String paymentUrl = STUB_PAYMENT_BASE_URL + transaction.getId();
    transaction.setTransactionReference("STUB-" + transaction.getId());
    paymentTransactionRepository.save(transaction);

    log.info(
        "Payment initiated for booking {} — transaction {} (stub)", bookingId, transaction.getId());

    // 6. Build response
    return PaymentInitiationResponse.builder()
        .transactionId(transaction.getId())
        .amount(transaction.getAmount())
        .paymentMethod(transaction.getPaymentMethod())
        .paymentUrl(paymentUrl)
        .expiresAt(booking.getPaymentDeadline())
        .build();
  }
}
