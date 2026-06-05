# Redis Lua Scripts in Travery Backend

This document explains the Lua scripting pattern used in `OtpService` for atomic Redis operations.

---

## Why Lua Scripts?

Redis executes Lua scripts **atomically** — no other command can run between any two
instructions inside a script. This is critical when an operation logically requires
reading and writing multiple keys together.

Without Lua: two separate Redis commands leave a window where the state is inconsistent:

```
Thread A:   INCR key   →  count = 1
            -- server crashes or slow GC here --
Thread A:   EXPIRE key -- never executes! Key lives forever.
```

With Lua: `INCR` and `EXPIRE` execute as a single indivisible step.

---

## Script 1 — OTP Verify (`verifyOtpByKey`)

**Purpose:** Compare an OTP, track bad attempts, and clean up on success — all atomically.

```lua
local otpKey    = KEYS[1];   -- e.g. "otp:register:user@email.com"
local attemptKey = KEYS[2];  -- e.g. "otp:attempt:register:user@email.com"
local inputOtp  = ARGV[1];   -- SHA-256 hash of user-provided OTP
local maxAttempts   = tonumber(ARGV[2]);
local otpTtlSeconds = tonumber(ARGV[3]);

local storedOtp = redis.call('GET', otpKey);
if not storedOtp then return -1; end;   -- -1: key missing or expired

if storedOtp == inputOtp then
    redis.call('DEL', otpKey);          -- remove OTP after success
    redis.call('DEL', attemptKey);      -- reset attempt counter
    return 1;                           --  1: success
end;

local attempts = redis.call('INCR', attemptKey);
if attempts == 1 then
    redis.call('EXPIRE', attemptKey, otpTtlSeconds);  -- TTL matches OTP TTL
end;

if attempts >= maxAttempts then
    redis.call('DEL', otpKey);          -- invalidate OTP on lockout
    redis.call('DEL', attemptKey);
    return -2;                          -- -2: locked out
end;

return 0;                               --  0: wrong OTP, still has attempts
```

**Return values:**

| Value | Meaning |
|-------|---------|
| `1`   | OTP matched — success |
| `0`   | Wrong OTP — attempts remaining |
| `-1`  | OTP expired or not found |
| `-2`  | Too many attempts — locked out |

---

## Script 2 — Reset Rate Limit (`checkResetRateLimit`)

**Purpose:** Enforce a maximum number of password-reset OTP requests per time window.

```lua
local count = redis.call('INCR', KEYS[1]);          -- increment counter
if count == 1 then
    redis.call('EXPIRE', KEYS[1], ARGV[1]);         -- set TTL on first call
end;
return count;
```

**KEYS[1]:** `"otp:reset:limit:{email}"`  
**ARGV[1]:** Window length in seconds (from `app.otp.reset-rate-limit-window-seconds`, default `3600`)

**Why this is safe vs. the old two-call approach:**

```
OLD (not atomic):
    INCR  key   -- count = 1
    EXPIRE key  -- could be skipped if crash happens here → key lives forever

NEW (atomic Lua):
    INCR + EXPIRE always happen together or not at all
```

---

## Java Integration

Both scripts are executed via Spring Data Redis `DefaultRedisScript<Long>`:

```java
DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(scriptText, Long.class);
Long result = redisTemplate.execute(
    redisScript,
    List.of(key1, key2),  // KEYS array
    arg1, arg2            // ARGV array (all strings)
);
```

- `KEYS` = Redis keys the script touches (required for Redis Cluster routing)
- `ARGV` = Dynamic values (thresholds, TTLs, input data)
- Always pass Redis **key names as KEYS**, not embedded in the script string — this
  ensures compatibility with Redis Cluster where keys must be on the same node.

---

## Key Naming Convention

| Prefix | Purpose | Example |
|--------|---------|---------|
| `otp:register:{email}` | Hashed registration OTP | `otp:register:user@x.com` |
| `otp:password-reset:{email}` | Hashed password-reset OTP | `otp:password-reset:user@x.com` |
| `otp:attempt:register:{email}` | Attempt counter for register flow | _(after TODO-AUTH-2 fix)_ |
| `otp:attempt:reset:{email}` | Attempt counter for reset flow | _(after TODO-AUTH-2 fix)_ |
| `otp:resend:{email}` | Resend cooldown flag | `otp:resend:user@x.com` |
| `otp:reset:limit:{email}` | Password-reset rate limit counter | `otp:reset:limit:user@x.com` |
| `blacklist:jti:{jti}` | Blacklisted access token JTI | `blacklist:jti:uuid-here` |
