package com.travery.traverybackend.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for VNPAY payment URL construction and checksum verification. Follows VNPAY API
 * v2.1.0 specification.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VnPayUtil {

  private static final String VNPAY_VERSION = "2.1.0";
  private static final String VNPAY_COMMAND = "pay";
  private static final String VNPAY_CURRENCY = "VND";
  private static final String VNPAY_LOCALE = "vn";
  private static final String VNPAY_ORDER_TYPE = "170000";
  private static final int AMOUNT_MULTIPLIER = 100;
  private static final String DATE_FORMAT = "yyyyMMddHHmmss";
  private static final String HMAC_ALGORITHM = "HmacSHA512";

  /**
   * Build a complete VNPAY payment URL.
   *
   * @param tmnCode VNPAY terminal code
   * @param payUrl VNPAY payment base URL
   * @param returnUrl Return URL after payment
   * @param secretKey VNPAY secret key for HMAC signing
   * @param txnRef Transaction reference (unique per day)
   * @param amount Payment amount in VND (will be multiplied by 100)
   * @param orderInfo Payment description (no diacritics, no special chars)
   * @param ipAddress Client IP address
   * @param timeoutMinutes Payment expiry in minutes
   * @return Complete VNPAY payment URL with signature
   */
  public static String buildPaymentUrl(
      String tmnCode,
      String payUrl,
      String returnUrl,
      String secretKey,
      String txnRef,
      long amount,
      String orderInfo,
      String ipAddress,
      int timeoutMinutes) {

    var vnCalendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    var formatter = new SimpleDateFormat(DATE_FORMAT);
    formatter.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));

    String createDate = formatter.format(vnCalendar.getTime());
    vnCalendar.add(Calendar.MINUTE, timeoutMinutes);
    String expireDate = formatter.format(vnCalendar.getTime());

    // TreeMap auto-sorts by key name (VNPAY requirement)
    Map<String, String> params = new TreeMap<>();
    params.put("vnp_Version", VNPAY_VERSION);
    params.put("vnp_Command", VNPAY_COMMAND);
    params.put("vnp_TmnCode", tmnCode);
    params.put("vnp_Amount", String.valueOf(amount * AMOUNT_MULTIPLIER));
    params.put("vnp_CurrCode", VNPAY_CURRENCY);
    params.put("vnp_TxnRef", txnRef);
    params.put("vnp_OrderInfo", orderInfo);
    params.put("vnp_OrderType", VNPAY_ORDER_TYPE);
    params.put("vnp_Locale", VNPAY_LOCALE);
    params.put("vnp_ReturnUrl", returnUrl);
    params.put("vnp_IpAddr", ipAddress);
    params.put("vnp_CreateDate", createDate);
    params.put("vnp_ExpireDate", expireDate);

    // Build hash data and query string
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();

    for (var entry : params.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (value != null && !value.isEmpty()) {
        if (!hashData.isEmpty()) {
          hashData.append('&');
          query.append('&');
        }
        hashData
            .append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
            .append('=')
            .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
        query
            .append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
            .append('=')
            .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
      }
    }

    String secureHash = hmacSHA512(secretKey, hashData.toString());
    query.append("&vnp_SecureHash=").append(secureHash);

    return payUrl + "?" + query;
  }

  /**
   * Validate the checksum of parameters received from VNPAY (IPN/Return URL).
   *
   * @param params All query parameters from VNPAY (excluding vnp_SecureHash)
   * @param receivedHash The vnp_SecureHash value from VNPAY
   * @param secretKey VNPAY secret key
   * @return true if checksum is valid
   */
  public static boolean validateChecksum(
      Map<String, String> params, String receivedHash, String secretKey) {
    // Remove hash fields before verification
    Map<String, String> sortedParams = new TreeMap<>(params);
    sortedParams.remove("vnp_SecureHash");
    sortedParams.remove("vnp_SecureHashType");

    StringBuilder hashData = new StringBuilder();
    for (var entry : sortedParams.entrySet()) {
      String value = entry.getValue();
      if (value != null && !value.isEmpty()) {
        if (!hashData.isEmpty()) {
          hashData.append('&');
        }
        hashData
            .append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
            .append('=')
            .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
      }
    }

    String calculatedHash = hmacSHA512(secretKey, hashData.toString());
    return calculatedHash.equalsIgnoreCase(receivedHash);
  }

  /**
   * Compute HMAC-SHA512 hash.
   *
   * @param key Secret key
   * @param data Data to hash
   * @return Hex-encoded hash string
   */
  public static String hmacSHA512(String key, String data) {
    try {
      Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
      hmac.init(secretKeySpec);
      byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

      StringBuilder hexString = new StringBuilder(hash.length * 2);
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to compute HMAC-SHA512", e);
    }
  }
}
