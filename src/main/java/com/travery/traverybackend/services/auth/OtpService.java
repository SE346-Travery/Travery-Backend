package com.travery.traverybackend.services.auth;

import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {
  private final RedisTemplate<String, String> redisTemplate;
  private final PasswordEncoder passwordEncoder;

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  @Value("${app.otp.ttl-minutes}")
  private long OTP_TTL_MINUTES;

  @Value("${app.otp.max-attempts}")
  private int MAX_ATTEMPTS;

  @Value("${app.otp.resend-cooldown-seconds}")
  private long RESEND_COOLDOWN_SECONDS;

  @Value("${app.otp.reset-max-per-hour}")
  private int MAX_RESET_PER_HOUR;

  @Value("${app.otp.length:6}")
  private int OTP_LENGTH;

  // Window in seconds for the password-reset rate limit (default: 1 hour = 3600s)
  @Value("${app.otp.reset-rate-limit-window-seconds:3600}")
  private long RESET_RATE_LIMIT_WINDOW_SECONDS;

  public String generateOtp() {
    int min = (int) Math.pow(10, OTP_LENGTH - 1);
    int max = (int) Math.pow(10, OTP_LENGTH) - 1;
    int otp = min + SECURE_RANDOM.nextInt(max - min + 1);
    return String.valueOf(otp);
  }

  // Lưu OTP cho việc đăng ký
  public void saveRegisterOtp(String email, String otp) {
    redisTemplate
        .opsForValue()
        .set(
            buildRegisterKey(email),
            fastHash(otp), // Save hashed otp instead of plaintext
            OTP_TTL_MINUTES,
            TimeUnit.MINUTES);
    redisTemplate.delete(buildAttemptKey(email));
  }

  // Lưu OTP cho việc đổi mật khẩu
  public void savePasswordResetOtp(String email, String otp) {
    redisTemplate
        .opsForValue()
        .set(buildPassResetKey(email), fastHash(otp), OTP_TTL_MINUTES, TimeUnit.MINUTES);
    redisTemplate.delete(buildAttemptKey(email)); // Reset counter mỗi lần gửi OTP mới
  }

  public boolean verifyRegisterOtp(String email, String inputOtp) {
    return verifyOtpByKey(buildRegisterKey(email), email, inputOtp);
  }

  public boolean verifyPasswordResetOtp(String email, String inputOtp) {
    return verifyOtpByKey(buildPassResetKey(email), email, inputOtp);
  }

  private boolean verifyOtpByKey(String otpKey, String email, String inputOtp) {
    String attemptKey = buildAttemptKey(email);
    String hashedInput = fastHash(inputOtp);

    // Lua Script
    String script =
        "local otpKey = KEYS[1];"
            + "local attemptKey = KEYS[2];"
            + "local inputOtp = ARGV[1];"
            + "local maxAttempts = tonumber(ARGV[2]);"
            + "local otpTtlSeconds = tonumber(ARGV[3]);"
            + "local storedOtp = redis.call('GET', otpKey);"
            + "if not storedOtp then return -1; end;"
            + // -1: Không tìm thấy / Hết hạn
            "if storedOtp == inputOtp then "
            + "   redis.call('DEL', otpKey);"
            + "   redis.call('DEL', attemptKey);"
            + "   return 1;"
            + // 1: Thành công
            "end;"
            + "local attempts = redis.call('INCR', attemptKey);"
            + "if attempts == 1 then redis.call('EXPIRE', attemptKey, otpTtlSeconds); end;"
            + "if attempts >= maxAttempts then "
            + "   redis.call('DEL', otpKey);"
            + "   redis.call('DEL', attemptKey);"
            + "   return -2;"
            + // -2: Vượt quá số lần thử (Bị block)
            "end;"
            + "return 0;"; // 0: Sai OTP nhưng vẫn còn cơ hội

    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
    redisScript.setScriptText(script);
    redisScript.setResultType(Long.class);

    // Quy đổi TTL ra giây cho Redis Script
    long ttlSeconds = TimeUnit.MINUTES.toSeconds(OTP_TTL_MINUTES);

    // Thực thi Script
    Long result =
        redisTemplate.execute(
            redisScript,
            Arrays.asList(otpKey, attemptKey), // KEYS[1], KEYS[2]
            hashedInput,
            String.valueOf(MAX_ATTEMPTS),
            String.valueOf(ttlSeconds) // ARGV[1], [2], [3]
            );

    if (result == null) return false;

    if (result == -2) {
      throw new BaseAppException(AuthErrorCode.TOO_MANY_ATTEMPTS); // Ném lỗi nếu nhập sai quá nhiều
    }

    return result == 1L; // Trả về true nếu thành công
  }

  public boolean canResend(String email) {
    return Boolean.FALSE.equals(redisTemplate.hasKey(buildResendKey(email)));
  }

  public void markResend(String email) {
    redisTemplate
        .opsForValue()
        .set(buildResendKey(email), "1", RESEND_COOLDOWN_SECONDS, TimeUnit.SECONDS);
  }

  public void checkResetRateLimit(String email) {
    // Atomic Lua: INCR + EXPIRE in a single round-trip.
    // If the server crashes between two separate calls, the key would persist forever.
    // This script guarantees the TTL is always set on the first increment.
    String script =
        "local count = redis.call('INCR', KEYS[1]);"
            + "if count == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end;"
            + "return count;";

    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
    Long count =
        redisTemplate.execute(
            redisScript,
            List.of("otp:reset:limit:" + email),
            String.valueOf(RESET_RATE_LIMIT_WINDOW_SECONDS));

    if (count != null && count > MAX_RESET_PER_HOUR) {
      throw new BaseAppException(AuthErrorCode.TOO_MANY_OTP_RESEND);
    }
  }

  private String fastHash(String data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedhash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encodedhash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found", e);
    }
  }

  //    private void incrementAttempt(String email, String otpKey) {
  //        String attemptKey = buildAttemptKey(email);
  //        Long attempts = redisTemplate.opsForValue().increment(attemptKey);
  //
  //        if (attempts != null && attempts == 1) {
  //            // Set TTL bằng với OTP TTL để attempt counter tự xóa cùng lúc
  //            redisTemplate.expire(attemptKey, OTP_TTL_MINUTES, TimeUnit.MINUTES);
  //        }
  //
  //        if (attempts != null && attempts >= MAX_ATTEMPTS) {
  //            // Xóa OTP khi vượt số lần thử — brute force không có tác dụng
  //            redisTemplate.delete(otpKey);
  //            redisTemplate.delete(attemptKey);
  //        }
  //    }

  private String buildRegisterKey(String email) {
    return "otp:register:" + email;
  }

  private String buildAttemptKey(String email) {
    return "otp:attempt:" + email;
  }

  private String buildResendKey(String email) {
    return "otp:resend:" + email;
  }

  private String buildPassResetKey(String email) {
    return "otp:password-reset:" + email;
  }
}
