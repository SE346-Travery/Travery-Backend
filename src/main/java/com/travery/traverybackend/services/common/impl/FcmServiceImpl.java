package com.travery.traverybackend.services.common.impl;

import com.google.firebase.messaging.*;
import com.travery.traverybackend.dtos.request.common.NotificationRequest;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.entities.user.UserDeviceToken;
import com.travery.traverybackend.repositories.user.UserDeviceTokenRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.CometChatService;
import com.travery.traverybackend.services.common.FcmService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

  private final UserDeviceTokenRepository userDeviceTokenRepository;
  private final UserRepository userRepository;
  private final CometChatService cometChatService;
  private final FirebaseMessaging firebaseMessaging;

  @Override
  @Transactional
  public void syncDeviceToken(String email, String fcmToken) {
    if (fcmToken == null || fcmToken.isBlank()) {
      return;
    }

    Optional<UserDeviceToken> existingToken = userDeviceTokenRepository.findByFcmToken(fcmToken);

    if (existingToken.isPresent()) {
      UserDeviceToken token = existingToken.get();
      if (!token.getEmail().equals(email)) {
        log.info("Updating existing FCM token to new email: {}", email);
        token.setEmail(email);
        userDeviceTokenRepository.save(token);
      }
    } else {
      log.info("Registering new FCM token for email: {}", email);
      UserDeviceToken newToken =
          UserDeviceToken.builder().email(email).fcmToken(fcmToken).build();
      userDeviceTokenRepository.save(newToken);
    }

    // Register token with CometChat
    userRepository
        .findByEmail(email)
        .ifPresent(
            user -> {
              cometChatService.registerPushToken(user.getCometchatUID(), fcmToken);
            });
  }

  @Override
  public void sendPushNotification(String email, NotificationRequest request) {
    List<String> tokens =
        userDeviceTokenRepository.findAllByEmail(email).stream()
            .map(UserDeviceToken::getFcmToken)
            .toList();

    if (tokens.isEmpty()) {
      log.debug("No tokens found for email: {}", email);
      return;
    }

    sendToTokens(tokens, request);
  }

  @Override
  public void sendMulticastNotification(List<String> emails, NotificationRequest request) {
    if (emails == null || emails.isEmpty()) return;

    List<String> tokens =
        emails.stream()
            .flatMap(
                email ->
                    userDeviceTokenRepository.findAllByEmail(email).stream()
                        .map(UserDeviceToken::getFcmToken))
            .distinct()
            .toList();

    if (tokens.isEmpty()) {
      log.debug("No tokens found for requested emails");
      return;
    }

    sendToTokens(tokens, request);
  }

  @Override
  @Transactional
  public void unregisterDeviceToken(String email, String fcmToken) {
    if (fcmToken == null || fcmToken.isBlank()) {
      return;
    }
    userDeviceTokenRepository.deleteByEmailAndFcmToken(email, fcmToken);
    log.info("Successfully unregistered FCM token for email: {}", email);
  }

  private void sendToTokens(List<String> tokens, NotificationRequest request) {
    Notification notification =
        Notification.builder()
            .setTitle(request.getTitle())
            .setBody(request.getBody())
            .setImage(request.getImageUrl())
            .build();

    MulticastMessage.Builder messageBuilder =
        MulticastMessage.builder().addAllTokens(tokens).setNotification(notification);

    if (request.getData() != null) {
      messageBuilder.putAllData(request.getData());
    }

    MulticastMessage message = messageBuilder.build();

    try {
      BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
      log.info("FCM multicast success count: {}", response.getSuccessCount());

      if (response.getFailureCount() > 0) {
        log.warn("FCM multicast failure count: {}", response.getFailureCount());
        // In a production app, we should ideally identify and remove invalid tokens here
      }
    } catch (FirebaseMessagingException e) {
      log.error("Critical error sending FCM multicast message", e);
    }
  }
}
