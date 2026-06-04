package com.travery.traverybackend.services.common;

import com.travery.traverybackend.dtos.response.common.NotificationListResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface NotificationHistoryService {
  NotificationListResponse getNotifications(String email, Pageable pageable);

  long getUnreadCount(String email);

  void markAsRead(UUID notificationId, String email);

  void markAllAsRead(String email);

  void deleteNotification(UUID notificationId, String email);
}
