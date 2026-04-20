package com.travery.traverybackend.dtos.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponse {
  private String accessToken;
  private String refreshToken;
}
