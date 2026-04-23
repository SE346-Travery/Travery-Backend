package com.travery.traverybackend.controllers;

import com.travery.traverybackend.dtos.request.auth.*;
import com.travery.traverybackend.dtos.request.auth.CreateStaffRequest;
import com.travery.traverybackend.dtos.response.auth.LoginResponse;
import com.travery.traverybackend.dtos.response.auth.RefreshResponse;
import com.travery.traverybackend.dtos.response.auth.RegisterResponse;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends AbstractBaseController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<SingleResponse<RegisterResponse>> signup(
      @Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    RegisterResponse responseData =
        RegisterResponse.builder().email(request.getEmail()).requiresOtp(true).build();
    return success(responseData, "User register successfully. Please verify your email.");
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<SuccessResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
    authService.verifyOtp(request);
    return success("Account verify successfully");
  }

  @PostMapping("/resend-otp")
  public ResponseEntity<SuccessResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
    authService.resendOtp(request);
    return success("OTP resent successfully.");
  }

  @PostMapping("/login")
  public ResponseEntity<SingleResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    LoginResponse responseData = authService.login(request);
    return success(responseData, "Login successful");
  }

  @PostMapping("/logout")
  public ResponseEntity<SuccessResponse> logout(
      @RequestHeader("Authorization") String authHeader,
      @Valid @RequestBody LogoutRequest request) {
    authService.logout(authHeader, request);
    return success("Logout successful");
  }

  @PostMapping("/refresh")
  public ResponseEntity<SingleResponse<RefreshResponse>> refresh(
      @Valid @RequestBody RefreshRequest request) {
    RefreshResponse response = authService.refresh(request);
    return success(response, "Refresh successful");
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<SuccessResponse> requestReset(
      @Valid @RequestBody ForgotPasswordRequest request) {
    authService.requestReset(request);
    return success("If email exists, OTP has been sent");
  }

  @PostMapping("/reset-password")
  public ResponseEntity<SuccessResponse> confirmReset(
      @Valid @RequestBody ResetPasswordRequest request) {
    authService.confirmReset(request);
    return success("Password reset successfully");
  }

  @PostMapping("/change-password")
  public ResponseEntity<SuccessResponse> changePassword(
      @AuthenticationPrincipal CustomUserDetails currentUser,
      @Valid @RequestBody ChangePasswordRequest request) {
    authService.changePassword(currentUser.getUserId(), request);
    return success("Password changed successfully. Please log in again on other devices.");
  }

  @PostMapping("/create-staff")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> createStaff(
      @Valid @RequestBody CreateStaffRequest request) {
    authService.createStaff(request);
    return success("Staff account created successfully");
  }
}
