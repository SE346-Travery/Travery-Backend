package com.travery.traverybackend.services.auth;

import com.travery.traverybackend.entities.auth.RefreshToken;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.UserErrorCode;
import com.travery.traverybackend.repositories.RefreshTokenRepository;
import com.travery.traverybackend.repositories.UserRepository;
import com.travery.traverybackend.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;

  public void save(String token, UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND, userId));

    Claims claims = jwtService.parseIgnoreExpiry(token);

    RefreshToken refreshToken =
        RefreshToken.builder()
            .token(token)
            .user(user)
            .expiryDate(jwtService.extractExpiration(claims).toInstant())
            .revoked(false)
            .build();

    refreshTokenRepository.save(refreshToken);
  }

  public void revoke(RefreshToken refreshToken) {
    refreshToken.setRevoked(true);
    refreshTokenRepository.save(refreshToken);
  }

  public void revokeAll(UUID userId) {
    refreshTokenRepository.revokeAllByUserId(userId);
  }
}
