package com.travery.traverybackend.controllers.common;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.common.NotificationListResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.common.NotificationHistoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController extends AbstractBaseController {

  private final NotificationHistoryService notificationHistoryService;

  @GetMapping
  public ResponseEntity<SingleResponse<NotificationListResponse>> getNotifications(
      @AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) {
    NotificationListResponse response =
        notificationHistoryService.getNotifications(userDetails.getEmail(), pageable);
    return success(response, "Notifications fetched successfully");
  }

  @GetMapping("/unread-count")
  public ResponseEntity<SingleResponse<Long>> getUnreadCount(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    long count = notificationHistoryService.getUnreadCount(userDetails.getEmail());
    return success(count, "Unread count fetched successfully");
  }

  @PutMapping("/{notificationId}/read")
  public ResponseEntity<SuccessResponse> markAsRead(
      @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID notificationId) {
    notificationHistoryService.markAsRead(notificationId, userDetails.getEmail());
    return success("Notification marked as read");
  }

  @PutMapping("/read-all")
  public ResponseEntity<SuccessResponse> markAllAsRead(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    notificationHistoryService.markAllAsRead(userDetails.getEmail());
    return success("All notifications marked as read");
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<SuccessResponse> deleteNotification(
      @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID notificationId) {
    notificationHistoryService.deleteNotification(notificationId, userDetails.getEmail());
    return success("Notification deleted successfully");
  }
}
