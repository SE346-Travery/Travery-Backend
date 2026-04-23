package com.travery.traverybackend.services.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.dtos.request.auth.AccountDeletionRequest;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.UserStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.repositories.UserRepository;
import com.travery.traverybackend.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private RefreshTokenService refreshTokenService;

  @Mock private TokenBlacklistService tokenBlacklistService;

  @Mock private JwtService jwtServiceImpl;

  @InjectMocks private AuthService authService;

  @Test
  public void deleteAccount_Success() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String password = "correctPassword";
    String authHeader = "Bearer mock-token";
    String token = "mock-token";

    AccountDeletionRequest request = new AccountDeletionRequest(password);

    User user =
        User.builder()
            .id(userId)
            .passwordHashed("hashedPassword")
            .status(UserStatus.ACTIVE)
            .build();

    Claims claims = new DefaultClaims(Map.of("jti", "mock-jti"));
    Date expiration = new Date(System.currentTimeMillis() + 100000);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(true);
    when(jwtServiceImpl.parseAndValidate(token)).thenReturn(claims);
    when(jwtServiceImpl.extractUserId(claims)).thenReturn(userId);
    when(jwtServiceImpl.extractJti(claims)).thenReturn("mock-jti");
    when(jwtServiceImpl.extractExpiration(claims)).thenReturn(expiration);

    // Act
    authService.deleteAccount(userId, request, authHeader);

    // Assert
    assertEquals(UserStatus.DELETED, user.getStatus());
    verify(userRepository).save(user);
    verify(refreshTokenService).revokeAll(userId);
    verify(tokenBlacklistService).blacklistAccessToken("mock-jti", expiration);
  }

  @Test
  public void deleteAccount_WrongPassword_ThrowsException() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String password = "wrongPassword";
    AccountDeletionRequest request = new AccountDeletionRequest(password);

    User user = User.builder().id(userId).passwordHashed("hashedPassword").build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(false);

    // Act & Assert
    assertThrows(
        BaseAppException.class, () -> authService.deleteAccount(userId, request, "Bearer token"));
  }
}
