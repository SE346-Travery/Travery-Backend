package com.travery.traverybackend.configs;

import com.travery.traverybackend.security.jwt.CustomAuthenticationEntryPoint;
import com.travery.traverybackend.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Bật @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {
  private static final String[] WHITE_LIST = {
    "/api/v1/auth/login",
    "/api/v1/auth/signup",
    "/api/v1/auth/verify-otp",
    "/api/v1/auth/resend-otp",
    "/api/v1/auth/refresh",
    "/api/v1/auth/forgot-password",
    "/api/v1/auth/reset-password",
    "/api/v1/payments/vnpay-ipn",
    "/api/v1/payments/vnpay-return",
    "/v3/api-docs/**",
    "/docs",
    "/scalar/**",
    "/scalar.html",
    "/actuator/prometheus"
  };

  private static final String[] PUBLIC_GET_ENDPOINTS = {"/api/v1/tours", "/api/v1/tours/**"};

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final DaoAuthenticationProvider daoAuthenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS)
                    .permitAll()
                    .requestMatchers(WHITE_LIST)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
        .authenticationProvider(daoAuthenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }
}
