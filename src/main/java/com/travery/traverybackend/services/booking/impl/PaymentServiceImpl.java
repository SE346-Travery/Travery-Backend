package com.travery.traverybackend.services.booking.impl;

import com.travery.traverybackend.configs.VnPayConfig;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
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
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.finance.PaymentTransactionRepository;
import com.travery.traverybackend.services.booking.PaymentService;
import com.travery.traverybackend.utils.VnPayUtil;
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
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final VnPayConfig vnPayConfig;

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

    // 4. Check for existing PENDING transaction (prevent double-click duplicates)
    var existingTransaction =
        paymentTransactionRepository.findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
            booking.getId(), BookingType.TOUR_BOOKING);

    if (existingTransaction.isPresent()) {
      PaymentTransaction existing = existingTransaction.get();
      if (existing.getStatus() == PaymentStatus.PENDING) {
        // Check if the VNPAY payment session is expired (created_at + timeout < now)
        boolean isSessionExpired =
            existing
                .getCreatedAt()
                .plusMinutes(vnPayConfig.getTimeout())
                .isBefore(LocalDateTime.now());

        if (isSessionExpired) {
          log.info(
              "Existing VNPAY session expired for transaction {}, marking as FAILED and allowing retry",
              existing.getId());
          existing.setStatus(PaymentStatus.FAILED);
          paymentTransactionRepository.save(existing);
        } else {
          // VNPAY session still active, return the existing URL
          String paymentUrl = buildVnPayUrl(existing, request.getIpAddress());
          return PaymentInitiationResponse.builder()
              .transactionId(existing.getId())
              .amount(existing.getAmount())
              .paymentUrl(paymentUrl)
              .expiresAt(booking.getPaymentDeadline())
              .build();
        }
      }
    }

    // 5. Create PaymentTransaction
    PaymentTransaction transaction =
        PaymentTransaction.builder()
            .user(booking.getUser())
            .bookingId(booking.getId())
            .bookingType(BookingType.TOUR_BOOKING)
            .amount(booking.getTotalPrice())
            .paymentMethod(PaymentMethod.VNPAY)
            .transactionType(TransactionType.PAYMENT)
            .status(PaymentStatus.PENDING)
            .build();
    transaction = paymentTransactionRepository.save(transaction);

    // 6. Build VNPAY payment URL (transactionReference set later by IPN callback)
    String paymentUrl = buildVnPayUrl(transaction, request.getIpAddress());

    log.info(
        "VNPAY payment initiated for booking {} — transaction {}", bookingId, transaction.getId());

    // 7. Build response
    return PaymentInitiationResponse.builder()
        .transactionId(transaction.getId())
        .amount(transaction.getAmount())
        .paymentUrl(paymentUrl)
        .expiresAt(booking.getPaymentDeadline())
        .build();
  }

  @Override
  @Transactional
  public Map<String, String> handleVnPayIpn(Map<String, String> params) {
    String secureHash = params.get("vnp_SecureHash");
    String txnRef = params.get("vnp_TxnRef");
    String vnpAmountStr = params.get("vnp_Amount");
    String vnpResponseCode = params.get("vnp_ResponseCode");

    // 1. Validate required parameter presence
    if (secureHash == null || txnRef == null || vnpAmountStr == null || vnpResponseCode == null) {
      log.warn("IPN: Missing required parameter(s). Params: {}", params);
      return ipnResponse("99", "Input data required");
    }

    // 2. Verify checksum
    if (!VnPayUtil.validateChecksum(params, secureHash, vnPayConfig.getSecretKey())) {
      log.warn("IPN: Invalid checksum for params: {}", params);
      return ipnResponse("97", "Invalid Checksum");
    }

    // 3. Find transaction by vnp_TxnRef (= our PaymentTransaction.id)
    UUID transactionId;
    try {
      transactionId = UUID.fromString(txnRef);
    } catch (IllegalArgumentException e) {
      log.warn("IPN: Invalid vnp_TxnRef format: {}", txnRef);
      return ipnResponse("01", "Order not found");
    }

    PaymentTransaction transaction =
        paymentTransactionRepository.findById(transactionId).orElse(null);
    if (transaction == null) {
      log.warn("IPN: Transaction not found: {}", transactionId);
      return ipnResponse("01", "Order not found");
    }

    // 4. Validate corresponding TourBooking existence before state updates (ensures data
    // consistency)
    TourBooking booking = tourBookingRepository.findById(transaction.getBookingId()).orElse(null);
    if (booking == null) {
      log.warn("IPN: Booking not found for transaction: {}", transactionId);
      return ipnResponse("01", "Order not found");
    }

    // 5. Verify amount matches and has valid numeric format
    long vnpAmount;
    try {
      vnpAmount = Long.parseLong(vnpAmountStr) / 100;
    } catch (NumberFormatException e) {
      log.warn("IPN: Invalid vnp_Amount format: {}", vnpAmountStr);
      return ipnResponse("04", "Invalid amount");
    }

    if (transaction.getAmount().longValue() != vnpAmount) {
      log.warn(
          "IPN: Amount mismatch for transaction {} — expected {} but got {}",
          transactionId,
          transaction.getAmount(),
          vnpAmount);
      return ipnResponse("04", "Invalid amount");
    }

    // 6. Check idempotency — already processed?
    if (transaction.getStatus() != PaymentStatus.PENDING) {
      log.info(
          "IPN: Transaction {} already processed (status={})",
          transactionId,
          transaction.getStatus());
      return ipnResponse("02", "Order already confirmed");
    }

    // 7. Update transaction with VNPAY reference
    String vnpTransactionNo = params.get("vnp_TransactionNo");
    String vnpBankCode = params.get("vnp_BankCode");
    transaction.setTransactionReference(vnpTransactionNo);

    // 8. Process based on response code
    VnPayResponseCode responseCode = VnPayResponseCode.fromCode(vnpResponseCode);

    if (responseCode.isSuccess()) {
      // Payment successful
      transaction.setStatus(PaymentStatus.SUCCESS);
      paymentTransactionRepository.save(transaction);

      // Update booking status to PAID
      booking.setStatus(BookingStatus.PAID);
      tourBookingRepository.save(booking);

      log.info(
          "IPN: Payment SUCCESS for transaction {} (booking={}, bank={}, vnpTxn={})",
          transactionId,
          transaction.getBookingId(),
          vnpBankCode,
          vnpTransactionNo);
    } else {
      // Payment failed
      transaction.setStatus(PaymentStatus.FAILED);
      paymentTransactionRepository.save(transaction);

      log.info(
          "IPN: Payment FAILED for transaction {} — code={} ({})",
          transactionId,
          vnpResponseCode,
          responseCode.getDescription());
    }

    return ipnResponse("00", "Confirm Success");
  }

  @Override
  public String handleVnPayReturn(Map<String, String> params) {
    String secureHash = params.get("vnp_SecureHash");
    String txnRef = params.get("vnp_TxnRef");
    String vnpResponseCode = params.get("vnp_ResponseCode");

    // Verify checksum — if invalid, redirect with error status
    boolean validChecksum =
        VnPayUtil.validateChecksum(params, secureHash, vnPayConfig.getSecretKey());

    VnPayResponseCode responseCode = VnPayResponseCode.fromCode(vnpResponseCode);
    String status = (validChecksum && responseCode.isSuccess()) ? "success" : "failed";

    // Build deeplink:
    // travery://payment-result?txnRef=xxx&status=success&responseCode=00
    String deeplink =
        String.format(
            "%s?txnRef=%s&status=%s&responseCode=%s",
            vnPayConfig.getDeeplinkScheme(), txnRef, status, vnpResponseCode);

    log.info(
        "VNPAY Return: txnRef={}, responseCode={}, redirecting to deeplink",
        txnRef,
        vnpResponseCode);
    return deeplink;
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
