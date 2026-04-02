package com.travery.traverybackend.security.jwt;

import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.AuthErrorCode;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.auth.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtService jwtServiceImpl;
  private final TokenBlacklistService tokenBlacklistService;
  private final UserDetailsService userDetailsService;
  private final HandlerExceptionResolver exceptionResolver;

  // Phải tự viết Constructor để dùng @Qualifier cho HandlerExceptionResolver
  public JwtAuthenticationFilter(
      JwtService jwtServiceImpl,
      TokenBlacklistService tokenBlacklistService,
      UserDetailsService userDetailsService,
      @Qualifier("handlerExceptionResolver")
          HandlerExceptionResolver exceptionResolver) // Để xử lí exception vì filter
        // chạy trước nên
        // AppExceptionHandler
        // không bắt được
      {
    this.jwtServiceImpl = jwtServiceImpl;
    this.tokenBlacklistService = tokenBlacklistService;
    this.userDetailsService = userDetailsService;
    this.exceptionResolver = exceptionResolver;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response); // Không có vẫn cho qua và bắt ở các filter sau
      return;
    }

    final String token = authHeader.substring(BEARER_PREFIX.length());

    try {
      Claims claims = jwtServiceImpl.parseAndValidate(token);

      if (!jwtServiceImpl.isAccessToken(claims)) {
        throw new BaseAppException(AuthErrorCode.TOKEN_TYPE_INVALID);
      }

      String jti = jwtServiceImpl.extractJti(claims);

      if (tokenBlacklistService.isBlacklisted(jti)) {
        throw new BaseAppException(AuthErrorCode.TOKEN_INVALID);
      }

      String username = jwtServiceImpl.extractUsername(claims);

      // Tránh override context nếu đã có
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        CustomUserDetails customUserDetails =
            (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        if (!customUserDetails.isEnabled()) {
          throw new BaseAppException(AuthErrorCode.USER_DISABLED);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null, // credentials — null after successful authentication
                customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    } catch (Exception ex) {
      // Delegate exception to HandlerExceptionResolver to be handled by
      // AppExceptionHandler
      exceptionResolver.resolveException(request, response, null, ex);
      return;
    }
    filterChain.doFilter(request, response);
  }
}
