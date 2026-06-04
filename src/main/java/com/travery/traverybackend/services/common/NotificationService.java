package com.travery.traverybackend.services.common;

import com.travery.traverybackend.enums.common.NotificationType;
import java.util.List;

public interface NotificationService {
  /** Send notification to a specific user and save to history. */
  void sendToUser(String email, NotificationType type, String title, String content, String dataId);

  /** Send notification to multiple users and save to history for each. */
  void sendToUsers(
      List<String> emails, NotificationType type, String title, String content, String dataId);
}
