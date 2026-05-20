package com.travery.traverybackend.services.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.configs.VnPayConfig;
import com.travery.traverybackend.dtos.request.booking.InitiatePaymentRequest;
import com.travery.traverybackend.dtos.response.booking.PaymentInitiationResponse;
import com.travery.traverybackend.entities.booking.TourBooking;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.PaymentStatus;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.finance.PaymentTransactionRepository;
import com.travery.traverybackend.services.booking.impl.PaymentServiceImpl;
import com.travery.traverybackend.utils.VnPayUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

  @Mock private TourBookingRepository tourBookingRepository;

  @Mock private PaymentTransactionRepository paymentTransactionRepository;

  @Mock private VnPayConfig vnPayConfig;

  @InjectMocks private PaymentServiceImpl paymentService;

  private UUID bookingId;
  private UUID userId;
  private TourBooking booking;
  private InitiatePaymentRequest initiateRequest;

  @BeforeEach
  void setUp() {
    bookingId = UUID.randomUUID();
    userId = UUID.randomUUID();

    booking =
        TourBooking.builder()
            .id(bookingId)
            .totalPrice(new BigDecimal("1000000"))
            .status(BookingStatus.PENDING)
            .paymentDeadline(LocalDateTime.now().plusMinutes(15))
            .build();

    initiateRequest =
        InitiatePaymentRequest.builder()
            .bookingId(bookingId)
            .amount(new BigDecimal("1000000"))
            .ipAddress("127.0.0.1")
            .build();
  }

  @Test
  void initiatePayment_Success_NewTransaction() {
    // Arrange
    when(tourBookingRepository.findByIdAndUser_Id(bookingId, userId))
        .thenReturn(Optional.of(booking));
    when(paymentTransactionRepository.findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
            bookingId, BookingType.TOUR_BOOKING))
        .thenReturn(Optional.empty());

    when(vnPayConfig.getTmnCode()).thenReturn("VNPAY001");
    when(vnPayConfig.getInitPaymentUrl())
        .thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
    when(vnPayConfig.getReturnUrl()).thenReturn("http://localhost:8080/return");
    when(vnPayConfig.getSecretKey()).thenReturn("SECRET");
    when(vnPayConfig.getTimeout()).thenReturn(15);

    PaymentTransaction savedTxn =
        PaymentTransaction.builder()
            .id(UUID.randomUUID())
            .bookingId(bookingId)
            .amount(booking.getTotalPrice())
            .status(PaymentStatus.PENDING)
            .build();

    when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(savedTxn);

    // Mock static VnPayUtil
    try (MockedStatic<VnPayUtil> mockedVnPayUtil = mockStatic(VnPayUtil.class)) {
      mockedVnPayUtil
          .when(
              () ->
                  VnPayUtil.buildPaymentUrl(
                      anyString(),
                      anyString(),
                      anyString(),
                      anyString(),
                      anyString(),
                      anyLong(),
                      anyString(),
                      anyString(),
                      anyInt()))
          .thenReturn("https://mock-vnpay-url.com");

      // Act
      PaymentInitiationResponse response =
          paymentService.initiatePayment(bookingId, initiateRequest, userId);

      // Assert
      assertNotNull(response);
      assertEquals("https://mock-vnpay-url.com", response.getPaymentUrl());
      assertEquals(savedTxn.getId(), response.getTransactionId());
      verify(paymentTransactionRepository).save(any(PaymentTransaction.class));
    }
  }

  @Test
  void initiatePayment_Success_ReturnExistingPendingTransaction() {
    // Arrange
    when(tourBookingRepository.findByIdAndUser_Id(bookingId, userId))
        .thenReturn(Optional.of(booking));

    PaymentTransaction existingTxn =
        PaymentTransaction.builder()
            .id(UUID.randomUUID())
            .bookingId(bookingId)
            .amount(booking.getTotalPrice())
            .status(PaymentStatus.PENDING)
            .build();
    existingTxn.setCreatedAt(LocalDateTime.now().minusMinutes(5)); // 5 minutes old (not expired)

    when(paymentTransactionRepository.findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
            bookingId, BookingType.TOUR_BOOKING))
        .thenReturn(Optional.of(existingTxn));

    when(vnPayConfig.getTmnCode()).thenReturn("VNPAY001");
    when(vnPayConfig.getInitPaymentUrl()).thenReturn("https://sandbox.vnpayment.vn/vpcpay.html");
    when(vnPayConfig.getReturnUrl()).thenReturn("http://localhost/return");
    when(vnPayConfig.getSecretKey()).thenReturn("SECRET");
    when(vnPayConfig.getTimeout()).thenReturn(15); // 15 mins expiry limit

    try (MockedStatic<VnPayUtil> mockedVnPayUtil = mockStatic(VnPayUtil.class)) {
      mockedVnPayUtil
          .when(
              () ->
                  VnPayUtil.buildPaymentUrl(
                      anyString(),
                      anyString(),
                      anyString(),
                      anyString(),
                      anyString(),
                      anyLong(),
                      anyString(),
                      anyString(),
                      anyInt()))
          .thenReturn("https://mock-vnpay-url.com");

      // Act
      PaymentInitiationResponse response =
          paymentService.initiatePayment(bookingId, initiateRequest, userId);

      // Assert
      assertNotNull(response);
      assertEquals("https://mock-vnpay-url.com", response.getPaymentUrl());
      assertEquals(existingTxn.getId(), response.getTransactionId());
      verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
    }
  }

  @Test
  void initiatePayment_Success_ExpiredPendingTransaction_CreatesNew() {
    // Arrange
    when(tourBookingRepository.findByIdAndUser_Id(bookingId, userId))
        .thenReturn(Optional.of(booking));

    PaymentTransaction expiredTxn =
        PaymentTransaction.builder()
            .id(UUID.randomUUID())
            .bookingId(bookingId)
            .amount(booking.getTotalPrice())
            .status(PaymentStatus.PENDING)
            .build();
    expiredTxn.setCreatedAt(LocalDateTime.now().minusMinutes(20)); // 20 minutes old (expired)

    when(paymentTransactionRepository.findFirstByBookingIdAndBookingTypeOrderByCreatedAtDesc(
            bookingId, BookingType.TOUR_BOOKING))
        .thenReturn(Optional.of(expiredTxn));

    when(vnPayConfig.getTmnCode()).thenReturn("VNPAY001");
    when(vnPayConfig.getInitPaymentUrl()).thenReturn("https://sandbox.vnpayment.vn/vpcpay.html");
    when(vnPayConfig.getReturnUrl()).thenReturn("http://localhost/return");
    when(vnPayConfig.getSecretKey()).thenReturn("SECRET");
    when(vnPayConfig.getTimeout()).thenReturn(15); // 15 mins expiry limit

    PaymentTransaction newTxn =
        PaymentTransaction.builder()
            .id(UUID.randomUUID())
            .bookingId(bookingId)
            .amount(booking.getTotalPrice())
            .status(PaymentStatus.PENDING)
            .build();

    // First save: updates expired to FAILED. Second save: saves new transaction.
    when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(newTxn);

    try (MockedStatic<VnPayUtil> mockedVnPayUtil = mockStatic(VnPayUtil.class)) {
      mockedVnPayUtil
          .when(
              () ->
                  VnPayUtil.buildPaymentUrl(
                      anyString(),
                      anyString(),
                      anyString(),
                      anyString(),
                      anyString(),
                      anyLong(),
                      anyString(),
                      anyString(),
                      anyInt()))
          .thenReturn("https://mock-vnpay-new-url.com");

      // Act
      PaymentInitiationResponse response =
          paymentService.initiatePayment(bookingId, initiateRequest, userId);

      // Assert
      assertNotNull(response);
      assertEquals("https://mock-vnpay-new-url.com", response.getPaymentUrl());
      assertEquals(PaymentStatus.FAILED, expiredTxn.getStatus()); // Expired gets updated
      verify(paymentTransactionRepository, times(2)).save(any(PaymentTransaction.class));
    }
  }

  @Test
  void handleVnPayIpn_Success() {
    // Arrange
    UUID transactionId = UUID.randomUUID();
    Map<String, String> ipnParams = new HashMap<>();
    ipnParams.put("vnp_SecureHash", "VALID_HASH");
    ipnParams.put("vnp_TxnRef", transactionId.toString());
    ipnParams.put("vnp_Amount", "100000000"); // 1,000,000 * 100
    ipnParams.put("vnp_ResponseCode", "00");
    ipnParams.put("vnp_TransactionNo", "VNP12345");
    ipnParams.put("vnp_BankCode", "NCB");

    when(vnPayConfig.getSecretKey()).thenReturn("SECRET");

    PaymentTransaction txn =
        PaymentTransaction.builder()
            .id(transactionId)
            .bookingId(bookingId)
            .amount(new BigDecimal("1000000"))
            .status(PaymentStatus.PENDING)
            .build();

    when(paymentTransactionRepository.findById(transactionId)).thenReturn(Optional.of(txn));
    when(tourBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

    try (MockedStatic<VnPayUtil> mockedVnPayUtil = mockStatic(VnPayUtil.class)) {
      mockedVnPayUtil
          .when(() -> VnPayUtil.validateChecksum(any(Map.class), anyString(), anyString()))
          .thenReturn(true);

      // Act
      Map<String, String> response = paymentService.handleVnPayIpn(ipnParams);

      // Assert
      assertEquals("00", response.get("RspCode"));
      assertEquals("Confirm Success", response.get("Message"));
      assertEquals(PaymentStatus.SUCCESS, txn.getStatus());
      assertEquals("VNP12345", txn.getTransactionReference());
      assertEquals(BookingStatus.PAID, booking.getStatus());
      verify(paymentTransactionRepository).save(txn);
      verify(tourBookingRepository).save(booking);
    }
  }

  @Test
  void handleVnPayIpn_InvalidChecksum() {
    // Arrange
    Map<String, String> ipnParams = new HashMap<>();
    ipnParams.put("vnp_SecureHash", "INVALID_HASH");
    ipnParams.put("vnp_TxnRef", UUID.randomUUID().toString());
    ipnParams.put("vnp_Amount", "100000000");
    ipnParams.put("vnp_ResponseCode", "00");

    when(vnPayConfig.getSecretKey()).thenReturn("SECRET");

    try (MockedStatic<VnPayUtil> mockedVnPayUtil = mockStatic(VnPayUtil.class)) {
      mockedVnPayUtil
          .when(() -> VnPayUtil.validateChecksum(any(Map.class), anyString(), anyString()))
          .thenReturn(false);

      // Act
      Map<String, String> response = paymentService.handleVnPayIpn(ipnParams);

      // Assert
      assertEquals("97", response.get("RspCode"));
      assertEquals("Invalid Checksum", response.get("Message"));
      verifyNoInteractions(paymentTransactionRepository, tourBookingRepository);
    }
  }

  @Test
  void handleVnPayIpn_BookingNotFound() {
    // Arrange
    UUID transactionId = UUID.randomUUID();
    Map<String, String> ipnParams = new HashMap<>();
    ipnParams.put("vnp_SecureHash", "VALID_HASH");
    ipnParams.put("vnp_TxnRef", transactionId.toString());
    ipnParams.put("vnp_Amount", "100000000");
    ipnParams.put("vnp_ResponseCode", "00");

    when(vnPayConfig.getSecretKey()).thenReturn("SECRET");

    PaymentTransaction txn =
        PaymentTransaction.builder()
            .id(transactionId)
            .bookingId(bookingId)
            .amount(new BigDecimal("1000000"))
            .status(PaymentStatus.PENDING)
            .build();

    when(paymentTransactionRepository.findById(transactionId)).thenReturn(Optional.of(txn));
    when(tourBookingRepository.findById(bookingId))
        .thenReturn(Optional.empty()); // Booking not found!

    try (MockedStatic<VnPayUtil> mockedVnPayUtil = mockStatic(VnPayUtil.class)) {
      mockedVnPayUtil
          .when(() -> VnPayUtil.validateChecksum(any(Map.class), anyString(), anyString()))
          .thenReturn(true);

      // Act
      Map<String, String> response = paymentService.handleVnPayIpn(ipnParams);

      // Assert
      assertEquals("01", response.get("RspCode"));
      assertEquals("Order not found", response.get("Message"));
      verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
    }
  }
}
