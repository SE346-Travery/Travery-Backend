package com.travery.traverybackend.security.user;

import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.AuthProvider;
import com.travery.traverybackend.enums.UserStatus;
import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
  // Chỉ bỏ vào mấy cái security cần biết, mấy cái field này final vì nó chỉ đc gán gt trong ctor và
  // sau đó không thay đổi (UserDetails nên là object bất biến (immutable))
  private final UUID userId; // Từ userId của user
  private final String email; // Từ email của user
  private final String
      password; // Từ passwordHashed của user  -> AuthenticationManager sẽ gọi để so sánh với
  // passwordEncoder.
  private final boolean isEnabled; // Từ status == ACTIVE của user
  private final Collection<? extends GrantedAuthority> authorities; // Từ role của user
  private final UserStatus status; // Thêm hai trường này để sử dụng trong AuthService
  private final AuthProvider authProvider;

  @Override
  public @Nonnull String getUsername() // Spring dùng nó để authentication.getName()
      {
    return email;
  }

  @Override
  public boolean isEnabled() {
    return this.isEnabled;
  }

  // Factory method
  public static CustomUserDetails from(User user) {
    List<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    return CustomUserDetails.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .password(user.getPasswordHashed())
        .isEnabled(user.getStatus() == UserStatus.ACTIVE)
        .authorities(authorities)
        .status(user.getStatus())
        .authProvider(user.getAuthProvider())
        .build();
  }
}
