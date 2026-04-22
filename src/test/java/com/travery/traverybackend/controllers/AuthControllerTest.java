package com.travery.traverybackend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travery.traverybackend.dtos.request.auth.AccountDeletionRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.security.jwt.CustomAuthenticationEntryPoint;
import com.travery.traverybackend.security.jwt.JwtAuthenticationFilter;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.auth.AuthService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ResponseFactory.class, AuthControllerTest.TestConfig.class})
public class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private AuthService authService;

  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @MockitoBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  @TestConfiguration
  static class TestConfig {
    @Bean
    public UserDetailsService userDetailsService() {
      return email ->
          CustomUserDetails.builder()
              .userId(TEST_USER_ID)
              .email(email)
              .isEnabled(true)
              .authorities(List.of(new SimpleGrantedAuthority("ROLE_TOURIST")))
              .build();
    }
  }

  @Test
  @WithUserDetails("test@travery.com")
  public void deleteAccount_ShouldReturnSuccess() throws Exception {
    AccountDeletionRequest request = new AccountDeletionRequest("password123");

    doNothing()
        .when(authService)
        .deleteAccount(
            eq(TEST_USER_ID), any(AccountDeletionRequest.class), eq("Bearer mock-token"));

    mockMvc
        .perform(
            post("/auth/account-deletion")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Account deleted successfully."));

    verify(authService).deleteAccount(eq(TEST_USER_ID), any(), eq("Bearer mock-token"));
  }
}
