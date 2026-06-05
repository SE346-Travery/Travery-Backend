package com.travery.traverybackend.dtos.response.common;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationListResponse {
  private Page<NotificationResponse> notifications;
  private long unreadCount;
}
