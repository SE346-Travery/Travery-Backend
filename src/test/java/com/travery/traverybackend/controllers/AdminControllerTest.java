package com.travery.traverybackend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travery.traverybackend.configs.SecurityConfig;
import com.travery.traverybackend.dtos.request.auth.CreateStaffRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.enums.UserRoles;
import com.travery.traverybackend.exception.AppExceptionHandler;
import com.travery.traverybackend.security.jwt.CustomAuthenticationEntryPoint;
import com.travery.traverybackend.security.jwt.JwtAuthenticationFilter;
import com.travery.traverybackend.security.jwt.JwtService;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.auth.AuthService;
import com.travery.traverybackend.services.auth.TokenBlacklistService;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import({
  ResponseFactory.class,
  SecurityConfig.class,
  AppExceptionHandler.class,
  AdminControllerTest.TestConfig.class
})
public class AdminControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private AuthService authService;

  @MockitoBean private JwtService jwtService;
  @MockitoBean private TokenBlacklistService tokenBlacklistService;
  @MockitoBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  @MockitoBean private DaoAuthenticationProvider daoAuthenticationProvider;

  private static final UUID TEST_ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

  @TestConfiguration
  static class TestConfig {
    @Bean
    public UserDetailsService userDetailsService() {
      return email -> {
        String role = email.contains("admin") ? "ROLE_ADMIN" : "ROLE_TOURIST";
        return CustomUserDetails.builder()
            .userId(TEST_ADMIN_ID)
            .email(email)
            .isEnabled(true)
            .authorities(List.of(new SimpleGrantedAuthority(role)))
            .build();
      };
    }
  }

  @Test
  @WithUserDetails("admin@travery.com")
  public void createStaff_ShouldReturnSuccess_WhenAdmin() throws Exception {
    CreateStaffRequest request =
        CreateStaffRequest.builder()
            .email("staff@example.com")
            .password("password123")
            .fullName("Staff Name")
            .role(UserRoles.COORDINATOR)
            .experienceYear(5)
            .build();

    doNothing().when(authService).createStaff(any(CreateStaffRequest.class));

    mockMvc
        .perform(
            post("/auth/create-staff")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Staff account created successfully"));
  }

  @Test
  @WithUserDetails("tourist@travery.com")
  public void createStaff_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
    CreateStaffRequest request =
        CreateStaffRequest.builder()
            .email("staff@example.com")
            .password("password123")
            .fullName("Staff Name")
            .role(UserRoles.COORDINATOR)
            .experienceYear(5)
            .build();

    mockMvc
        .perform(
            post("/auth/create-staff")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createStaff_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
    doAnswer(
            invocation -> {
              HttpServletResponse response = invocation.getArgument(1);
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              return null;
            })
        .when(customAuthenticationEntryPoint)
        .commence(any(), any(), any());

    CreateStaffRequest request =
        CreateStaffRequest.builder()
            .email("staff@example.com")
            .password("password123")
            .fullName("Staff Name")
            .role(UserRoles.COORDINATOR)
            .experienceYear(5)
            .build();

    mockMvc
        .perform(
            post("/auth/create-staff")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
