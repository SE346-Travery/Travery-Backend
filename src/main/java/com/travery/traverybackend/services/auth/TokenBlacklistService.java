package com.travery.traverybackend.services.auth;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
  private final RedisTemplate<String, String> redisTemplate;

  private static final String PREFIX = "blacklist:jti:";

  /**
   * Kiểm tra JTI có trong blacklist không. Được gọi từ JwtAuthenticationFilter sau khi đã parse
   * Claims.
   */
  public boolean isBlacklisted(String jti) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
  }

  /**
   * Đưa access token vào blacklist cho đến khi hết hạn. Caller truyền jti và expiration từ Claims
   * đã parse — service này không cần biết về JWT, không inject JwtService nữa.
   *
   * <p>TTL = thời gian còn lại của token → Redis tự xóa khi token hết hạn, không cần job cleanup.
   */
  public void blacklistAccessToken(String jti, Date expiration) {
    long ttl = expiration.getTime() - System.currentTimeMillis();
    if (ttl > 0) {
      redisTemplate.opsForValue().set(PREFIX + jti, "1", ttl, TimeUnit.MILLISECONDS);
    }
    // ttl <= 0: token is expired, do not blacklist
  }
}
