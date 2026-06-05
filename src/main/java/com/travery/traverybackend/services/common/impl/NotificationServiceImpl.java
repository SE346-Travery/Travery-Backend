package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.request.common.NotificationRequest;
import com.travery.traverybackend.entities.common.Notification;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.repositories.common.NotificationRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.FcmService;
import com.travery.traverybackend.services.common.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final FcmService fcmService;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void sendToUser(
      String email, NotificationType type, String title, String content, String dataId) {

    NotificationRequest pushRequest =
        NotificationRequest.builder().title(title).body(content).build();

    // 1. Send Push
    fcmService.sendPushNotification(email, pushRequest);

    // 2. Save History
    saveHistory(email, type, title, content, dataId);
  }

  @Override
  @Transactional
  public void sendToUsers(
      List<String> emails, NotificationType type, String title, String content, String dataId) {

    NotificationRequest pushRequest =
        NotificationRequest.builder().title(title).body(content).build();

    // 1. Send Multicast Push
    fcmService.sendMulticastNotification(emails, pushRequest);

    // 2. Save History for all in bulk
    List<Notification> notifications = userRepository.findByEmailIn(emails).stream()
        .map(user -> {
            Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .content(content)
                .dataId(dataId)
                .isRead(false)
                .build();
            return notification;
        })
        .toList();
    notificationRepository.saveAll(notifications);
  }

  private void saveHistory(
      String email, NotificationType type, String title, String content, String dataId) {

    userRepository
        .findByEmail(email)
        .ifPresent(
            user -> {
              Notification notification =
                  Notification.builder()
                      .user(user)
                      .type(type)
                      .title(title)
                      .content(content)
                      .dataId(dataId)
                      .isRead(false)
                      .build();
              notificationRepository.save(notification);
            });
  }
}
