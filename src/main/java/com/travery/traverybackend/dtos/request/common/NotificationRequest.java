package com.travery.traverybackend.dtos.request.common;

import java.util.Map;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
  private String title;
  private String body;
  private String imageUrl;
  private Map<String, String> data;
}
