# TODO — Travery Backend

Backlog of known issues and planned improvements. Items are grouped by area.

---

## 🔐 Authentication & Security

### [TODO-AUTH-1] Add Login Rate Limiting
**Area:** `OtpService` / `AuthService`
**Priority:** High — brute force protection is missing for `POST /auth/login`

The `/auth/login` endpoint has no rate limiting. All other sensitive operations
(`resend-otp`, `forgot-password`) already have Redis-based rate limits.

**Suggested approach:** Add a `checkLoginRateLimit(email)` method in `OtpService`
(or a dedicated `LoginRateLimitService`) using the same pattern as `checkResetRateLimit()`.
Call it at the start of `AuthService.login()` before `authenticationManager.authenticate()`.

Add to `application-dev.yml`:
```yaml
app:
  auth:
    max-login-attempts: 10           # max attempts per window
    login-rate-window-seconds: 900   # 15 minutes
```

---

### [TODO-AUTH-2] OTP Attempt Key Namespace Collision
**Area:** `OtpService.buildAttemptKey()`
**Priority:** Medium

The attempt counter key `otp:attempt:{email}` is shared between the register flow and
the password-reset flow. Failing attempts in one flow consumes attempts from the other.

**Fix:** Add a `flow` parameter to `buildAttemptKey()`:

```java
private String buildAttemptKey(String email, String flow) {
    return "otp:attempt:" + flow + ":" + email;
}
// Call sites:
// verifyRegisterOtp  → buildAttemptKey(email, "register")
// verifyPasswordResetOtp → buildAttemptKey(email, "reset")
```

This also requires updating the Lua script KEYS array in `verifyOtpByKey()`.

---

### [TODO-AUTH-3] Decouple JwtService Interface from jjwt Library
**Area:** `JwtService.java`, `JwtServiceImpl.java`
**Priority:** Low

The `JwtService` interface exposes `io.jsonwebtoken.Claims` directly in its method
signatures, creating a tight coupling between the JWT library and all callers.
If the library is ever changed (e.g., to Nimbus or auth0-java-jwt), every caller breaks.

**Fix:** Define a library-agnostic domain record:

```java
// New file: security/jwt/ParsedToken.java
public record ParsedToken(
    String username,
    UUID userId,
    String type,
    String jti,
    Date expiration
) {}
```

Then update `JwtService` interface to use `ParsedToken` instead of `Claims`.

---

### [TODO-AUTH-4] Avoid DB Hit Per Authenticated Request
**Area:** `JwtAuthenticationFilter.java`
**Priority:** Low — optimize when performance becomes a concern

Currently every authenticated request triggers `userDetailsService.loadUserByUsername(email)`,
which hits the `users` table. Under load this creates significant DB pressure.

**Fix:** Build `CustomUserDetails` directly from JWT claims (which already contain `userId`,
`email`, and `authorities`), bypassing the DB lookup entirely.

Trade-off: A banned user with a valid non-expired/non-blacklisted token may remain
active until the token expires. Acceptable because the token TTL is short (15 min)
and the JTI blacklist already handles explicit logout.

---

### [TODO-AUTH-5] Add Security Audit Logging
**Area:** `AuthService`, `JwtAuthenticationFilter`
**Priority:** Medium — required before production

Currently there is no structured logging for security events. The following should
be recorded with enough context for an audit trail:

| Event | Log Level | Fields to Include |
|-------|-----------|-------------------|
| Login success | INFO | userId, email, IP, timestamp |
| Login failure | WARN | email, IP, reason, timestamp |
| Token blacklisted (logout) | INFO | userId, jti, expiry |
| Blacklisted token used | WARN | jti, IP, endpoint |
| Password reset requested | INFO | email, IP, timestamp |
| Password reset completed | INFO | userId, IP, timestamp |
| OTP max attempts exceeded | WARN | email, flow, IP |

Use SLF4J with MDC to attach `userId` and `requestId` to all log lines within a request.

---

## 📧 Email

### [TODO-EMAIL-1] Create HTML Email Templates for OTP
**Area:** `EmailService.java`
**Priority:** Medium — improve user experience

The current OTP emails are plain text single-liners. Replace with:
- HTML template (using Thymeleaf or FreeMarker) with Travery branding
- Clear call-to-action section showing the OTP
- Expiry notice ("This OTP expires in X minutes")
- Footer with company info and unsubscribe link

The plaintext OTP is passed to the async email task. This is fine since the OTP
is already hashed in Redis and the email value is only in memory for the duration
of the async thread. However, avoid ever logging the raw OTP value.

---

## 🗄️ Database

### [TODO-DB-1] Add Flyway Migration Scripts
**Area:** `src/main/resources/db/migration/`
**Priority:** High — required for production-safe deployments

Currently the schema is managed by Hibernate `ddl-auto: update`, which is unsafe for
production (cannot handle column drops, renames, or type changes safely).

Create Flyway migration files:
```
V1__create_users_table.sql
V2__create_refresh_tokens_table.sql
```

Set `spring.jpa.hibernate.ddl-auto: validate` once Flyway is in place.
