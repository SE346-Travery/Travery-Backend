package com.travery.traverybackend.security.jwt;

import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.AuthErrorCode;
import com.travery.traverybackend.security.user.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

  @Value("${app.jwt.secret}")
  private String secretKey;

  @Value("${app.jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Value("${app.jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;

  private SecretKey signingKey;

  @PostConstruct
  public void init() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      throw new IllegalStateException(
          "Jwt secret must be at least 32 bytes long (now: " + keyBytes.length + " bytes).");
    }
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public String generateAccessToken(UserDetails userDetails) {
    return buildToken(userDetails, accessTokenExpiration, "access");
  }

  @Override
  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(userDetails, refreshTokenExpiration, "refresh");
  }

  private String buildToken(UserDetails userDetails, long expiration, String type) {
    CustomUserDetails user = (CustomUserDetails) userDetails;
    long now = System.currentTimeMillis();

    return Jwts.builder()
        .subject(userDetails.getUsername())
        .id(UUID.randomUUID().toString())
        .claim("userId", user.getUserId())
        .claim("type", type)
        .claim(
            "authorities",
            user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
        .issuedAt(new Date(now))
        .expiration(new Date(now + expiration))
        .compact();
  }

  @Override
  public String extractUsername(Claims claims) {
    return claims.getSubject();
  }

  @Override
  public UUID extractUserId(Claims claims) {
    return UUID.fromString(claims.get("userId", String.class));
  }

  @Override
  public String extractType(Claims claims) {
    return claims.get("type", String.class);
  }

  @Override
  public String extractJti(Claims claims) {
    return claims.getId();
  }

  @Override
  public Date extractExpiration(Claims claims) {
    return claims.getExpiration();
  }

  @Override
  public boolean isAccessToken(Claims claims) {
    return "access".equals(claims.get("type", String.class));
  }

  @Override
  public boolean isRefreshToken(Claims claims) {
    return "refresh".equals(claims.get("type", String.class));
  }

  @Override
  public Claims parseAndValidate(String token) {
    try {
      return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      throw new BaseAppException(AuthErrorCode.TOKEN_EXPIRED);
    } catch (JwtException e) {
      throw new BaseAppException(AuthErrorCode.TOKEN_INVALID);
    }
  }

  @Override
  public Claims parseIgnoreExpiry(String token) {
    try {
      return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      return e.getClaims(); // lấy claims dù expired — chữ ký vẫn đã được verify
    } catch (JwtException e) {
      throw new BaseAppException(AuthErrorCode.TOKEN_INVALID);
    }
  }
}
