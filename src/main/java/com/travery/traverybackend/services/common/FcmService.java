package com.travery.traverybackend.services.common;

import com.travery.traverybackend.dtos.request.common.NotificationRequest;
import java.util.List;

public interface FcmService {
  /**
   * Register or update a device token for a specific email. If the token already exists but belongs
   * to a different email, it will be updated.
   */
  void syncDeviceToken(String email, String fcmToken);

  /** Send a notification to all devices registered for a specific email. */
  void sendPushNotification(String email, NotificationRequest request);

  /** Send a notification to multiple emails simultaneously. */
  void sendMulticastNotification(List<String> emails, NotificationRequest request);

  /** Unregister a device token for a specific email. */
  void unregisterDeviceToken(String email, String fcmToken);
}
