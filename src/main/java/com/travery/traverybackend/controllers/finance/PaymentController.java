package com.travery.traverybackend.controllers.finance;

import com.travery.traverybackend.services.booking.PaymentService;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payment callback endpoints for VNPAY. These endpoints are called by VNPAY servers or browser
 * redirects and do NOT require authentication.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

  private final PaymentService paymentService;

  /**
   * VNPAY IPN (Instant Payment Notification) callback. Called by VNPAY server after payment
   * processing. Updates payment transaction and booking status. Returns RspCode/Message JSON for
   * VNPAY.
   */
  @GetMapping("/vnpay-ipn")
  public ResponseEntity<Map<String, String>> handleVnPayIpn(
      @RequestParam Map<String, String> params) {
    Map<String, String> response = paymentService.handleVnPayIpn(params);
    return ResponseEntity.ok(response);
  }

  /**
   * VNPAY Return URL. Browser redirects here after user completes payment on VNPAY. Verifies
   * checksum and redirects to mobile app via deeplink. Does NOT update DB (IPN handles that).
   */
  @GetMapping("/vnpay-return")
  public ResponseEntity<Void> handleVnPayReturn(@RequestParam Map<String, String> params) {
    String deeplinkUrl = paymentService.handleVnPayReturn(params);
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(deeplinkUrl)).build();
  }
}
