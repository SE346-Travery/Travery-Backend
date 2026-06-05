# Change Password Flow

> **Endpoint:** `POST /auth/change-password`  
> **Auth Required:** ✅ Yes — Bearer access token  
> **Use Case:** An authenticated user who knows their current password wants to set a new one.  
> **Do NOT confuse with:** `POST /auth/reset-password` (forgot-password flow — no current password needed, uses OTP instead)

---

## Request

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Body:**
```json
{
  "currentPassword": "myOldPassword123",
  "newPassword":     "myNewPassword456",
  "confirmPassword": "myNewPassword456"
}
```

| Field             | Type   | Constraints                    |
|-------------------|--------|-------------------------------|
| `currentPassword` | String | Required                       |
| `newPassword`     | String | Required, min 8 characters     |
| `confirmPassword` | String | Required                       |

---

## Response

**Success — 200 OK:**
```json
{
  "message": "Password changed successfully. Please log in again on other devices."
}
```

**Error responses:**

| HTTP Status | Error Code | Message | Condition |
|-------------|-----------|---------|-----------|
| `401` | `AUTH_101` | Token is invalid | Access token missing or malformed |
| `401` | `AUTH_106` | Token expired | Access token expired |
| `400` | `AUTH_303` | Current password is incorrect | `currentPassword` doesn't match stored hash |
| `400` | `AUTH_304` | New password must be different from the current password | `newPassword` == `currentPassword` |
| `400` | `AUTH_305` | Passwords do not match | `newPassword` != `confirmPassword` |
| `400` | Validation error | Field-level constraint violation | Blank fields, password too short |

---

## Flow Diagram

```
Client                              Server
  │                                   │
  │  POST /auth/change-password        │
  │  Authorization: Bearer <token>     │
  │  { currentPassword, newPassword,  │
  │    confirmPassword }               │
  │ ─────────────────────────────────>│
  │                                   │
  │                    [JwtAuthenticationFilter]
  │                    ① parse & validate access token
  │                    ② check JTI not blacklisted
  │                    ③ load user → set SecurityContext
  │                                   │
  │                    [AuthController]
  │                    ④ inject @AuthenticationPrincipal
  │                       → extract userId (no extra DB call)
  │                                   │
  │                    [AuthService.changePassword()]
  │                    ⑤ fetch User from DB by userId
  │                    ⑥ passwordEncoder.matches(
  │                         currentPassword, storedHash)
  │                       → throws AUTH_303 if wrong
  │                    ⑦ check newPassword != currentPassword
  │                       → throws AUTH_304 if same
  │                    ⑧ check newPassword == confirmPassword
  │                       → throws AUTH_305 if mismatch
  │                    ⑨ encode & save newPassword
  │                    ⑩ refreshTokenService.revokeAll(userId)
  │                       → all refresh tokens in DB → revoked=true
  │                                   │
  │  200 OK                           │
  │  { "message": "Password changed..."}
  │ <─────────────────────────────────│
```

---

## Security Design Decisions

### Why is `@AuthenticationPrincipal` used instead of a request body email?

The user identity comes from the **verified JWT principal** already set in `SecurityContext` by `JwtAuthenticationFilter`. This means:
- No extra DB call needed to look up the user by email
- No risk of a user changing another user's password by sending a different email
- The `userId` from the principal is trusted — it was extracted from a cryptographically signed JWT

### Why are all refresh tokens revoked but the access token is NOT blacklisted?

| Token | Action | Reason |
|-------|--------|--------|
| Refresh tokens | All revoked in DB immediately | Forces re-authentication on all other devices |
| Current access token | Left valid until natural expiry | TTL is only 15 min; blacklisting would log out the current session too |

This is a deliberate UX trade-off: the user stays logged in on their current device and is not forced to log in again immediately after changing their password. Other devices will need to re-authenticate when their refresh tokens are rejected.

> [!NOTE]
> If your use case requires immediate invalidation of ALL sessions (including the current one), add `tokenBlacklistService.blacklistAccessToken(jti, expiration)` to step ⑩.

### Why is "new password same as current" explicitly checked?

BCrypt is a one-way hash — you cannot decrypt it. The only way to compare is `passwordEncoder.matches()`. This check prevents users from "changing" their password to the same value, which would be a no-op but could confuse users into thinking something changed.

---

## Difference vs. Reset Password

| Feature | `change-password` | `reset-password` |
|---------|:-----------------:|:----------------:|
| Requires login | ✅ | ❌ |
| Requires current password | ✅ | ❌ |
| Requires OTP | ❌ | ✅ |
| Revokes all sessions | ✅ | ✅ |
| Use case | User remembers password, wants to update it | User forgot password |
