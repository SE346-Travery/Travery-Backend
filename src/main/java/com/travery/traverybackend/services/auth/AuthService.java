package com.travery.traverybackend.services.auth;

import com.travery.traverybackend.dtos.request.auth.*;
import com.travery.traverybackend.dtos.response.auth.LoginResponse;
import com.travery.traverybackend.dtos.response.auth.RefreshResponse;
import com.travery.traverybackend.entities.auth.RefreshToken;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.entities.user.Receptionist;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.auth.AuthProvider;
import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.AuthErrorCode;
import com.travery.traverybackend.exception.error.UserErrorCode;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.repositories.auth.RefreshTokenRepository;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.security.jwt.JwtService;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.common.FcmService;
import com.travery.traverybackend.services.common.NotificationService;
import com.travery.traverybackend.enums.common.NotificationType;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private static final String BEARER_PREFIX = "Bearer ";
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final OtpService otpService;
  private final EmailService emailService;
  private final RefreshTokenService refreshTokenService;
  private final TokenBlacklistService tokenBlacklistService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtServiceImpl;
  private final AuthenticationManager authenticationManager;
  private final HotelRepository hotelRepository;
  private final FcmService fcmService;
  private final NotificationService notificationService;

  public void register(RegisterRequest request) {
    Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

    if (existingUser.isPresent()) {
      User user = existingUser.get();

      if (user.getStatus() == UserStatus.ACTIVE) {
        throw new BaseAppException(UserErrorCode.USER_EXISTED);
      }

      // Update user info
      user.setFullName(request.getFullName());
      user.setPasswordHashed(passwordEncoder.encode(request.getPassword()));
      userRepository.save(user);

      if (!otpService.canResend(request.getEmail())) {
        throw new BaseAppException(AuthErrorCode.OTP_WAIT_BEFORE_RESEND);
      }

      sendOtp(user.getEmail());
      return;
    }

    User user =
        Tourist.builder()
            .email(request.getEmail())
            .fullName(request.getFullName())
            .role(
                UserRoles.TOURIST) // Only tourists may self-register; other roles are admin-created
            .passwordHashed(passwordEncoder.encode(request.getPassword()))
            .authProvider(AuthProvider.LOCAL)
            .status(UserStatus.PENDING)
            .build();

    // Save user with PENDING status
    userRepository.save(user);

    // Sync FCM token if provided
    fcmService.syncDeviceToken(user.getEmail(), request.getFcmToken());

    sendOtp(user.getEmail());
  }

  public void verifyOtp(VerifyOtpRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(
                () -> new BaseAppException(UserErrorCode.USER_NOT_FOUND, request.getEmail()));

    if (user.getStatus() == UserStatus.ACTIVE) {
      throw new BaseAppException(UserErrorCode.USER_ALREADY_ACTIVE);
    }

    boolean isVerified = otpService.verifyRegisterOtp(request.getEmail(), request.getOtp());

    if (!isVerified) {
      throw new BaseAppException(AuthErrorCode.OTP_INVALID);
    }

    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);
  }

  public void resendOtp(ResendOtpRequest request) {
    String email = request.getEmail();
    if (!otpService.canResend(email)) {
      throw new BaseAppException(AuthErrorCode.OTP_WAIT_BEFORE_RESEND);
    }
    sendOtp(email);
  }

  public LoginResponse login(LoginRequest request) {
    // ===== SECURITY CHECK =====
    // Tạo `UsernamePasswordAuthenticationToken`-> gửi vào AuthenticationManager ->
    // Gọi
    // DaoAuthenticationProvider -> loadUserByUsername + so sánh password
    Authentication authentication;
    try {
      authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (DisabledException ex) {
      User user =
          userRepository
              .findByEmail(request.getEmail())
              .orElseThrow(() -> new BaseAppException(AuthErrorCode.BAD_CREDENTIALS));
      if (user.getStatus() == UserStatus.PENDING) {
        throw new BaseAppException(AuthErrorCode.USER_NOT_VERIFIED);
      }
      if (user.getStatus() == UserStatus.BANNED) {
        throw new BaseAppException(UserErrorCode.USER_BANNED);
      }
      if (user.getStatus() == UserStatus.DELETED) {
        throw new BaseAppException(AuthErrorCode.USER_DISABLED);
      }
      throw ex;
    }

    // ===== BUSINESS CHECK =====
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    // ===== GENERATE TOKEN =====
    String accessToken = jwtServiceImpl.generateAccessToken(customUserDetails);
    String refreshToken = jwtServiceImpl.generateRefreshToken(customUserDetails);

    refreshTokenService.save(refreshToken, customUserDetails.getUserId());

    // Sync FCM token if provided
    fcmService.syncDeviceToken(customUserDetails.getEmail(), request.getFcmToken());

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .cometchatUid(customUserDetails.getCometchatUid())
        .build();
  }

  @Transactional
  public RefreshResponse refresh(RefreshRequest request) {

    String token = request.getRefreshToken();
    Claims claims = jwtServiceImpl.parseIgnoreExpiry(token);

    if (!jwtServiceImpl.extractType(claims).equals("refresh")) {
      throw new BaseAppException(AuthErrorCode.TOKEN_TYPE_INVALID);
    }

    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new BaseAppException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

    if (refreshToken.isRevoked()) {
      throw new BaseAppException(AuthErrorCode.REFRESH_TOKEN_REVOKED);
    }

    if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
      throw new BaseAppException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    User user = refreshToken.getUser();

    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new BaseAppException(UserErrorCode.USER_BANNED);
    }

    CustomUserDetails userDetails = CustomUserDetails.from(user);

    // Generate new tokens
    String newAccessToken = jwtServiceImpl.generateAccessToken(userDetails);
    String newRefreshToken = jwtServiceImpl.generateRefreshToken(userDetails);

    // Revoke token cũ (rotate refresh token — token cũ không dùng được nữa)
    refreshTokenService.revoke(refreshToken);

    // Lưu token mới — parse để lấy expiration
    refreshTokenService.save(newRefreshToken, user.getId());

    return RefreshResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }

  @Transactional
  public void logout(String authHeader, LogoutRequest request) {
    // Access token
    if (authHeader == null
        || !authHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
      throw new BaseAppException(AuthErrorCode.TOKEN_INVALID);
    }
    String accessToken = authHeader.substring(BEARER_PREFIX.length()).trim();

    Claims accessClaims = jwtServiceImpl.parseAndValidate(accessToken);
    String jti = jwtServiceImpl.extractJti(accessClaims);
    Date expiration = jwtServiceImpl.extractExpiration(accessClaims);
    UUID accessUserId = jwtServiceImpl.extractUserId(accessClaims);

    // Blacklist access token
    tokenBlacklistService.blacklistAccessToken(jti, expiration);

    // Refresh token
    String refreshTokenStr = request.getRefreshToken();
    Claims refreshClaims = jwtServiceImpl.parseIgnoreExpiry(refreshTokenStr);

    if (!jwtServiceImpl.isRefreshToken(refreshClaims)) {
      throw new BaseAppException(AuthErrorCode.TOKEN_TYPE_INVALID);
    }

    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(refreshTokenStr)
            .orElseThrow(() -> new BaseAppException(AuthErrorCode.TOKEN_INVALID));

    // Ownership check - Prevent revoking other users' tokens
    if (refreshToken.getUser() == null || !refreshToken.getUser().getId().equals(accessUserId)) {
      throw new BaseAppException(AuthErrorCode.TOKEN_INVALID);
    }

    refreshTokenService.revoke(refreshToken);

    // Unregister FCM token if provided
    fcmService.unregisterDeviceToken(refreshToken.getUser().getEmail(), request.getFcmToken());
  }

  public void requestReset(ForgotPasswordRequest request) {
    String email = request.getEmail();

    otpService.checkResetRateLimit(email);

    Optional<User> optionalUser = userRepository.findByEmail(email);

    // Don't reveal whether user not found (security best practice)
    if (optionalUser.isEmpty() || optionalUser.get().getStatus() != UserStatus.ACTIVE) {
      return;
    }

    if (!otpService.canResend(email)) {
      throw new BaseAppException(AuthErrorCode.OTP_WAIT_BEFORE_RESEND);
    }

    String otp = otpService.generateOtp();
    otpService.savePasswordResetOtp(email, otp);
    otpService.markResend(email);
    emailService.sendResetPasswordOtp(email, otp);
  }

  @Transactional
  public void confirmReset(ResetPasswordRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(
                () -> new BaseAppException(UserErrorCode.USER_NOT_FOUND, request.getEmail()));

    if (!otpService.verifyPasswordResetOtp(user.getEmail(), request.getOtp())) {
      throw new BaseAppException(AuthErrorCode.OTP_INVALID);
    }

    user.setPasswordHashed(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    // Send Security Alert Notification
    notificationService.sendToUser(
        user.getEmail(),
        NotificationType.SECURITY_ALERT,
        "Thay đổi mật khẩu thành công",
        "Mật khẩu của bạn vừa được thay đổi. Nếu bạn không thực hiện việc này, vui lòng liên hệ CSKH ngay lập tức.",
        null);

    // Revoke all refresh tokens — force to log in again in all devices
    refreshTokenService.revokeAll(user.getId());
  }

  @Transactional
  public void changePassword(UUID userId, ChangePasswordRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND, userId));

    // 1. Verify current password
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHashed())) {
      throw new BaseAppException(AuthErrorCode.INVALID_CURRENT_PASSWORD);
    }

    // 2. New password must be different from the current one
    if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHashed())) {
      throw new BaseAppException(AuthErrorCode.NEW_PASSWORD_SAME_AS_CURRENT);
    }

    // 3. Confirm password must match new password
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new BaseAppException(AuthErrorCode.PASSWORDS_DO_NOT_MATCH);
    }

    // 4. Save new password hash
    user.setPasswordHashed(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    // Send Security Alert Notification
    notificationService.sendToUser(
        user.getEmail(),
        NotificationType.SECURITY_ALERT,
        "Thay đổi mật khẩu thành công",
        "Mật khẩu của bạn vừa được thay đổi. Nếu bạn không thực hiện việc này, vui lòng liên hệ CSKH ngay lập tức.",
        null);

    // 5. Revoke all refresh tokens — forces re-login on other devices
    // The current access token remains valid until it expires (short TTL).
    refreshTokenService.revokeAll(userId);
  }

  @Transactional
  public void deleteAccount(UUID userId, AccountDeletionRequest request, String authHeader) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND, userId));

    // 1. Verify password
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHashed())) {
      throw new BaseAppException(AuthErrorCode.INVALID_CURRENT_PASSWORD);
    }

    // 2. Soft delete
    user.setStatus(UserStatus.DELETED);
    userRepository.save(user);

    // 3. Revoke all refresh tokens
    refreshTokenService.revokeAll(userId);

    // 4. Blacklist current access token
    if (authHeader == null
        || !authHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
      throw new BaseAppException(AuthErrorCode.TOKEN_INVALID);
    }
    String accessToken = authHeader.substring(BEARER_PREFIX.length()).trim();

    Claims claims = jwtServiceImpl.parseAndValidate(accessToken);

    // Security check: Ensure token belongs to the user being deleted
    if (!userId.equals(jwtServiceImpl.extractUserId(claims))) {
      throw new BaseAppException(AuthErrorCode.TOKEN_INVALID);
    }

    tokenBlacklistService.blacklistAccessToken(
        jwtServiceImpl.extractJti(claims), jwtServiceImpl.extractExpiration(claims));
  }

  @Transactional
  public void createStaff(CreateStaffRequest request) {
    // Consistency check: Only throw if an ACTIVE user already exists with this
    // email
    userRepository
        .findByEmail(request.getEmail())
        .ifPresent(
            existingUser -> {
              if (existingUser.getStatus() == UserStatus.ACTIVE) {
                throw new BaseAppException(UserErrorCode.USER_EXISTED);
              }
            });

    User user =
        switch (request.getRole()) {
          case COORDINATOR -> Coordinator.builder().department(request.getDepartment()).build();
          case GUIDE -> Guide.builder().guideLicense(request.getGuideLicense()).build();
          case RECEPTIONIST -> {
            if (request.getHotelId() == null) {
              throw new BaseAppException(
                  WebErrorCode.BAD_REQUEST, "Hotel ID is required for Receptionist");
            }
            Hotel hotel =
                hotelRepository
                    .findById(request.getHotelId())
                    .orElseThrow(
                        () ->
                            new BaseAppException(
                                WebErrorCode.BAD_REQUEST, "Hotel not found for Receptionist"));
            yield Receptionist.builder().hotel(hotel).build();
          }
          default ->
              throw new BaseAppException(
                  WebErrorCode.BAD_REQUEST, "Invalid role for staff creation");
        };

    user.setEmail(request.getEmail());
    user.setFullName(request.getFullName());
    user.setPasswordHashed(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());
    user.setStatus(UserStatus.ACTIVE);
    user.setAuthProvider(AuthProvider.LOCAL);

    userRepository.save(user);
  }

  private void sendOtp(String email) {
    String otp = otpService.generateOtp();
    otpService.saveRegisterOtp(email, otp);
    emailService.sendOtp(email, otp);
    otpService.markResend(email);
  }
}
