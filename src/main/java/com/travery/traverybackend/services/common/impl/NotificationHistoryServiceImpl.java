package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.response.common.NotificationListResponse;
import com.travery.traverybackend.dtos.response.common.NotificationResponse;
import com.travery.traverybackend.entities.common.Notification;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.SystemErrorCode;
import com.travery.traverybackend.mappers.NotificationMapper;
import com.travery.traverybackend.repositories.common.NotificationRepository;
import com.travery.traverybackend.services.common.NotificationHistoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationHistoryServiceImpl implements NotificationHistoryService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  @Transactional(readOnly = true)
  public NotificationListResponse getNotifications(String email, Pageable pageable) {
    Page<Notification> notifications =
        notificationRepository.findAllByUserEmailOrderByCreatedAtDesc(email, pageable);
    long unreadCount = notificationRepository.countByUserEmailAndIsReadFalse(email);

    Page<NotificationResponse> responsePage = notifications.map(notificationMapper::toResponse);

    return NotificationListResponse.builder()
        .notifications(responsePage)
        .unreadCount(unreadCount)
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public long getUnreadCount(String email) {
    return notificationRepository.countByUserEmailAndIsReadFalse(email);
  }

  @Override
  @Transactional
  public void markAsRead(UUID notificationId, String email) {
    int updatedRows = notificationRepository.markAsRead(notificationId, email);
    if (updatedRows == 0) {
      throw new BaseAppException(SystemErrorCode.RESOURCE_NOT_FOUND);
    }
  }

  @Override
  @Transactional
  public void markAllAsRead(String email) {
    notificationRepository.markAllAsRead(email);
  }

  @Override
  @Transactional
  public void deleteNotification(UUID notificationId, String email) {
    notificationRepository.deleteByIdAndUserEmail(notificationId, email);
  }
}
