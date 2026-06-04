package com.travery.traverybackend.controllers.common;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travery.traverybackend.configs.SecurityConfig;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import com.travery.traverybackend.enums.common.ChatSessionStatus;
import com.travery.traverybackend.exception.AppExceptionHandler;
import com.travery.traverybackend.security.jwt.CustomAuthenticationEntryPoint;
import com.travery.traverybackend.security.jwt.JwtAuthenticationFilter;
import com.travery.traverybackend.security.jwt.JwtService;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.auth.TokenBlacklistService;
import com.travery.traverybackend.services.common.ChatSessionService;
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

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc
@Import({
  ResponseFactory.class,
  SecurityConfig.class,
  JwtAuthenticationFilter.class,
  AppExceptionHandler.class,
  ChatControllerTest.TestConfig.class
})
public class ChatControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ChatSessionService chatSessionService;

  @MockitoBean private JwtService jwtService;
  @MockitoBean private TokenBlacklistService tokenBlacklistService;
  @MockitoBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  @MockitoBean private DaoAuthenticationProvider daoAuthenticationProvider;

  private static final UUID TEST_USER_ID = UUID.randomUUID();

  @TestConfiguration
  static class TestConfig {
    @Bean
    public UserDetailsService userDetailsService() {
      return email ->
          CustomUserDetails.builder()
              .userId(TEST_USER_ID)
              .email(email)
              .authorities(List.of(new SimpleGrantedAuthority("ROLE_TOURIST")))
              .build();
    }
  }

  @Test
  @WithUserDetails("user@example.com")
  public void initiateChat_ShouldReturnSuccess() throws Exception {
    ChatSessionResponse response =
        ChatSessionResponse.builder()
            .id(UUID.randomUUID())
            .cometchatGuid("guid123")
            .userId(TEST_USER_ID)
            .status(ChatSessionStatus.OPEN)
            .build();

    when(chatSessionService.initiateCustomTourChat(any(UUID.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/chats/initiate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.cometchatGuid").value("guid123"))
        .andExpect(jsonPath("$.message").value("Custom tour consultation initiated successfully"));
  }
}
