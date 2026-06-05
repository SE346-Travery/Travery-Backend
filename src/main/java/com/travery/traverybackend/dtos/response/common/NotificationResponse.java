package com.travery.traverybackend.dtos.response.common;

import com.travery.traverybackend.enums.common.NotificationType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
  private UUID id;
  private String title;
  private String content;
  private NotificationType type;
  private String dataId;
  private Boolean isRead;
  private LocalDateTime createdAt;
}
