package com.travery.traverybackend.security.jwt;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
  String generateAccessToken(UserDetails userDetails);

  String generateRefreshToken(UserDetails userDetails);

  Claims parseAndValidate(String token);

  Claims parseIgnoreExpiry(String token);

  String extractUsername(Claims claims);

  UUID extractUserId(Claims claims);

  String extractType(Claims claims);

  String extractJti(Claims claims);

  Date extractExpiration(Claims claims);

  boolean isAccessToken(Claims claims);

  boolean isRefreshToken(Claims claims);
}
