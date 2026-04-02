package com.travery.traverybackend.exception.error;

import com.travery.traverybackend.exception.AppErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements AppErrorCode {

  // === TOKEN ===
  TOKEN_INVALID("AUTH_101", "Token is invalid", HttpStatus.UNAUTHORIZED),
  TOKEN_TYPE_INVALID("AUTH_102", "Invalid token type", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_NOT_FOUND("AUTH_103", "Refresh token not found", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_REVOKED("AUTH_104", "Refresh token has been revoked", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_EXPIRED("AUTH_105", "Refresh token expired", HttpStatus.UNAUTHORIZED),
  TOKEN_EXPIRED("AUTH_106", "Token expired", HttpStatus.UNAUTHORIZED),
  USER_DISABLED("AUTH_107", "User disabled", HttpStatus.UNAUTHORIZED),

  // === OTP ===
  OTP_INVALID("AUTH_201", "Invalid OTP", HttpStatus.BAD_REQUEST),
  TOO_MANY_ATTEMPTS(
      "AUTH_202", "Too many attempts. Please try again later", HttpStatus.BAD_REQUEST),
  TOO_MANY_OTP_RESEND("AUTH_203", "Too many OTP resend attempts", HttpStatus.BAD_REQUEST),
  OTP_WAIT_BEFORE_RESEND(
      "AUTH_204",
      "Please wait before requesting another OTP",
      HttpStatus.TOO_MANY_REQUESTS), // Dùng 429 Too Many Requests

  // === CREDENTIALS & STATUS ===
  BAD_CREDENTIALS("AUTH_301", "Wrong email or password", HttpStatus.UNAUTHORIZED),
  USER_NOT_VERIFIED("AUTH_302", "Email not verified", HttpStatus.FORBIDDEN),
  INVALID_CURRENT_PASSWORD("AUTH_303", "Current password is incorrect", HttpStatus.BAD_REQUEST),
  NEW_PASSWORD_SAME_AS_CURRENT(
      "AUTH_304",
      "New password must be different from the current password",
      HttpStatus.BAD_REQUEST),
  PASSWORDS_DO_NOT_MATCH("AUTH_305", "Passwords do not match", HttpStatus.BAD_REQUEST);

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;
}
