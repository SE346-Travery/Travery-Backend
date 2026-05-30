package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.configs.VnPayConfig;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.entities.booking.HotelBooking;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.PaymentMethod;
import com.travery.traverybackend.enums.finance.PaymentStatus;
import com.travery.traverybackend.enums.finance.TransactionType;
import com.travery.traverybackend.enums.finance.VnPayResponseCode;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.BookingErrorCode;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.finance.PaymentTransactionRepository;
import com.travery.traverybackend.services.booking.PaymentService;
import com.travery.traverybackend.services.common.ChatSessionService;
import com.travery.traverybackend.utils.VnPayUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  private final TourBookingRepository tourBookingRepository;
  private final HotelBookingRepository hotelBookingRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final ChatSessionService chatSessionService;
  private final VnPayConfig vnPayConfig;

  @Override
  @Transactional
  public PaymentInitiationResponse initiatePayment(
      UUID bookingId, InitiatePaymentRequest request, UUID userId, BookingType bookingType) {

    // 1. Load booking and verify ownership/status/deadline
    BigDecimal amount;
    LocalDateTime deadline;
    com.travery.traverybackend.entities.user.User user;

    if (bookingType == BookingType.TOUR_BOOKING) {
      TourBooking booking =
          tourBookingRepository
              .findByIdAndUser_Id(bookingId, userId)
              .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));
      if (booking.getStatus() != BookingStatus.PENDING) {
        throw new BaseAppException(BookingErrorCode.BOOKING_NOT_PENDING);
      }
      if (booking.getPaymentDeadline().isBefore(LocalDateTime.now())) {
        throw new BaseAppException(BookingErrorCode.PAYMENT_DEADLINE_EXPIRED);
      }
      amount = booking.getTotalPrice();
      deadline = booking.getPaymentDeadline();
      user = booking.getUser();
    } else {
      HotelBooking booking =
          hotelBookingRepository
              .findById(bookingId)
              .orElseThrow(() -> new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND));
      if (!booking.getUser().getId().equals(userId)) {
        throw new BaseAppException(BookingErrorCode.BOOKING_NOT_FOUND);
      }
      if (booking.getStatus() != BookingStatus.PENDING) {
        throw new BaseAppException(BookingErrorCode.BOOKING_NOT_PENDING);
      }
      if (booking.getPaymentDeadline().isBefore(LocalDateTime.now())) {
        throw new BaseAppException(BookingErrorCode.PAYMENT_DEADLINE_EXPIRED);
      }
      amount = booking.getTotalPrice();
      deadline = booking.getPaymentDeadline();
      user = booking.getUser();
    }

    // 2. Check for existing PENDING transaction
    var existingTransaction =
        paymentTransactionRepository.findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
            bookingId, bookingType);

    if (existingTransaction.isPresent()) {
      PaymentTransaction existing = existingTransaction.get();
      if (existing.getStatus() == PaymentStatus.PENDING) {
        boolean isSessionExpired =
            existing
                .getCreatedAt()
                .plusMinutes(vnPayConfig.getTimeout())
                .isBefore(LocalDateTime.now());

        if (isSessionExpired) {
          existing.setStatus(PaymentStatus.FAILED);
          paymentTransactionRepository.save(existing);
        } else {
          String paymentUrl = buildVnPayUrl(existing, request.getIpAddress());
          return PaymentInitiationResponse.builder()
              .transactionId(existing.getId())
              .amount(existing.getAmount())
              .paymentUrl(paymentUrl)
              .expiresAt(deadline)
              .build();
        }
      }
    }

    // 3. Create PaymentTransaction
    PaymentTransaction transaction =
        PaymentTransaction.builder()
            .user(user)
            .bookingId(bookingId)
            .bookingType(bookingType)
            .amount(amount)
            .paymentMethod(PaymentMethod.VNPAY)
            .transactionType(TransactionType.PAYMENT)
            .status(PaymentStatus.PENDING)
            .build();
    transaction = paymentTransactionRepository.save(transaction);

    String paymentUrl = buildVnPayUrl(transaction, request.getIpAddress());

    log.info(
        "VNPAY payment initiated for {} {} — transaction {}",
        bookingType,
        bookingId,
        transaction.getId());

    return PaymentInitiationResponse.builder()
        .transactionId(transaction.getId())
        .amount(transaction.getAmount())
        .paymentUrl(paymentUrl)
        .expiresAt(deadline)
        .build();
  }

  @Override
  @Transactional
  public Map<String, String> handleVnPayIpn(Map<String, String> params) {
    String secureHash = params.get("vnp_SecureHash");
    String txnRef = params.get("vnp_TxnRef");
    String vnpAmountStr = params.get("vnp_Amount");
    String vnpResponseCode = params.get("vnp_ResponseCode");

    if (secureHash == null || txnRef == null || vnpAmountStr == null || vnpResponseCode == null) {
      return ipnResponse("99", "Input data required");
    }

    if (!VnPayUtil.validateChecksum(params, secureHash, vnPayConfig.getSecretKey())) {
      return ipnResponse("97", "Invalid Checksum");
    }

    UUID transactionId;
    try {
      transactionId = UUID.fromString(txnRef);
    } catch (IllegalArgumentException e) {
      return ipnResponse("01", "Order not found");
    }

    PaymentTransaction transaction =
        paymentTransactionRepository.findById(transactionId).orElse(null);
    if (transaction == null) {
      return ipnResponse("01", "Order not found");
    }

    long vnpAmount;
    try {
      vnpAmount = Long.parseLong(vnpAmountStr) / 100;
    } catch (NumberFormatException e) {
      return ipnResponse("04", "Invalid amount");
    }

    if (transaction.getAmount().longValue() != vnpAmount) {
      return ipnResponse("04", "Invalid amount");
    }

    if (transaction.getStatus() != PaymentStatus.PENDING) {
      return ipnResponse("02", "Order already confirmed");
    }

    String vnpTransactionNo = params.get("vnp_TransactionNo");
    transaction.setTransactionReference(vnpTransactionNo);

    VnPayResponseCode responseCode = VnPayResponseCode.fromCode(vnpResponseCode);

    if (responseCode.isSuccess()) {
      transaction.setStatus(PaymentStatus.SUCCESS);
      paymentTransactionRepository.save(transaction);

      // Update booking status based on type
      if (transaction.getBookingType() == BookingType.TOUR_BOOKING) {
        TourBooking booking =
            tourBookingRepository.findById(transaction.getBookingId()).orElse(null);
        if (booking != null) {
          booking.setStatus(BookingStatus.PAID);
          tourBookingRepository.save(booking);
          // Add to chat
          try {
            chatSessionService.addUserToChat(
                booking.getTourInstance().getId(), booking.getUser().getId());
          } catch (Exception e) {
            log.error("Failed to add user to tour chat", e);
          }
        }
      } else {
        HotelBooking booking =
            hotelBookingRepository.findById(transaction.getBookingId()).orElse(null);
        if (booking != null) {
          booking.setStatus(BookingStatus.PAID);
          hotelBookingRepository.save(booking);
        }
      }

      log.info(
          "IPN: Payment SUCCESS for {} transaction {}",
          transaction.getBookingType(),
          transactionId);
    } else {
      transaction.setStatus(PaymentStatus.FAILED);
      paymentTransactionRepository.save(transaction);
      log.info("IPN: Payment FAILED for transaction {}", transactionId);
    }

    return ipnResponse("00", "Confirm Success");
  }

  @Override
  public String handleVnPayReturn(Map<String, String> params) {
    String secureHash = params.get("vnp_SecureHash");
    String txnRef = params.get("vnp_TxnRef");
    String vnpResponseCode = params.get("vnp_ResponseCode");

    boolean validChecksum =
        VnPayUtil.validateChecksum(params, secureHash, vnPayConfig.getSecretKey());

    VnPayResponseCode responseCode = VnPayResponseCode.fromCode(vnpResponseCode);
    String status = (validChecksum && responseCode.isSuccess()) ? "success" : "failed";

    return String.format(
        "%s?txnRef=%s&status=%s&responseCode=%s",
        vnPayConfig.getDeeplinkScheme(), txnRef, status, vnpResponseCode);
  }

  private Map<String, String> ipnResponse(String rspCode, String message) {
    return Map.of("RspCode", rspCode, "Message", message);
  }

  private String buildVnPayUrl(PaymentTransaction transaction, String ipAddress) {
    String orderInfo =
        String.format("Thanh toan booking %s", transaction.getBookingId().toString());

    return VnPayUtil.buildPaymentUrl(
        vnPayConfig.getTmnCode(),
        vnPayConfig.getInitPaymentUrl(),
        vnPayConfig.getReturnUrl(),
        vnPayConfig.getSecretKey(),
        transaction.getId().toString(),
        transaction.getAmount().longValue(),
        orderInfo,
        ipAddress,
        vnPayConfig.getTimeout());
  }
}
