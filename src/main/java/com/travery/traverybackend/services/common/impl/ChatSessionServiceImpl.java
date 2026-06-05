package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import com.travery.traverybackend.entities.common.ChatSession;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.common.ChatSessionStatus;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.common.ChatSessionRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.ChatSessionService;
import com.travery.traverybackend.services.common.CometChatService;
import com.travery.traverybackend.services.common.NotificationService;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements ChatSessionService {

  private final ChatSessionRepository chatSessionRepository;
  private final TourInstanceRepository tourInstanceRepository;
  private final UserRepository userRepository;
  private final TourBookingRepository tourBookingRepository;
  private final CometChatService cometChatService;
  private final NotificationService notificationService;

  private static final AtomicInteger coordinatorIndex = new AtomicInteger(0);

  @Override
  @Transactional
  public ChatSessionResponse initiateCustomTourChat(UUID userId) {
    User tourist =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

    // 1. Round Robin to find active coordinator
    List<User> activeCoordinators = userRepository.findAllActiveCoordinators();
    if (activeCoordinators.isEmpty()) {
      throw new BaseAppException(WebErrorCode.NOT_FOUND, "No active coordinators available");
    }

    int index =
        (coordinatorIndex.getAndIncrement() & Integer.MAX_VALUE) % activeCoordinators.size();
    Coordinator coordinator = (Coordinator) activeCoordinators.get(index);

    // 2. Setup CometChat
    String guid = "consult_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8);
    String name = "Tư vấn Tour: " + tourist.getFullName();

    cometChatService.createGroup(guid, name);
    cometChatService.addMemberToGroup(guid, coordinator.getCometchatUID(), "admins");
    cometChatService.addMemberToGroup(guid, tourist.getCometchatUID(), "participants");

    // 3. Save Session
    ChatSession chatSession =
        ChatSession.builder()
            .user(tourist)
            .coordinator(coordinator)
            .cometchatGuid(guid)
            .status(ChatSessionStatus.OPEN)
            .build();

    chatSession = chatSessionRepository.save(chatSession);

    // Notify Coordinator
    notificationService.sendToUser(
        coordinator.getEmail(),
        NotificationType.CUSTOM_TOUR_CHAT_ASSIGNED,
        "Yêu cầu tư vấn thiết kế Tour mới",
        String.format(
            "Khách hàng %s vừa yêu cầu tư vấn. Hãy vào nhóm chat để hỗ trợ ngay nhé!",
            tourist.getFullName()),
        chatSession.getId().toString());

    return toResponse(chatSession);
  }

  @Override
  @Transactional
  public ChatSessionResponse getOrCreateInstanceChatSession(UUID tourInstanceId) {
    return chatSessionRepository
        .findByTourInstanceId(tourInstanceId)
        .map(this::toResponse)
        .orElseGet(() -> toResponse(createInstanceChatSession(tourInstanceId)));
  }

  private ChatSession createInstanceChatSession(UUID tourInstanceId) {
    TourInstance instance =
        tourInstanceRepository
            .findById(tourInstanceId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found"));

    if (instance.getStatus() != TourInstanceStatus.OPEN) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Group chat can only be created for OPEN tour instances");
    }

    String shortId = instance.getId().toString().substring(0, 8).toUpperCase();
    String dateStr = instance.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    String guid = "tour_instance_" + instance.getId();
    String name = String.format("Nhóm %s - %s", shortId, dateStr);

    User guide = instance.getGuide();
    Coordinator coordinator = instance.getCoordinator();

    cometChatService.createGroup(guid, name);

    if (guide != null) {
      cometChatService.addMemberToGroup(guid, guide.getCometchatUID(), "admins");
    }
    if (coordinator != null) {
      cometChatService.addMemberToGroup(guid, coordinator.getCometchatUID(), "admins");
    }

    ChatSession chatSession =
        ChatSession.builder()
            .coordinator(coordinator)
            .tourInstance(instance)
            .tour(instance.getTour())
            .cometchatGuid(guid)
            .status(ChatSessionStatus.OPEN)
            .build();

    chatSession = chatSessionRepository.save(chatSession);

    // Notify All Members (Guide, Coordinator, Paid Tourists)
    List<String> memberEmails = new ArrayList<>();
    if (guide != null) memberEmails.add(guide.getEmail());
    if (coordinator != null) memberEmails.add(coordinator.getEmail());

    tourBookingRepository
        .findByTourInstanceIdAndStatus(instance.getId(), BookingStatus.PAID)
        .forEach(booking -> memberEmails.add(booking.getUser().getEmail()));

    if (!memberEmails.isEmpty()) {
      notificationService.sendToUsers(
          memberEmails,
          NotificationType.GROUP_CHAT_CREATED,
          "Nhóm chat đoàn đã mở!",
          String.format(
              "Nhóm chat cho chuyến đi %s đã sẵn sàng. Hãy vào làm quen với mọi người nhé!",
              instance.getTour().getName()),
          instance.getId().toString());
    }

    return chatSession;
  }

  @Override
  @Transactional
  public void addUserToChat(UUID tourInstanceId, UUID userId) {
    ChatSession chatSession =
        chatSessionRepository
            .findByTourInstanceId(tourInstanceId)
            .orElseGet(() -> createInstanceChatSession(tourInstanceId));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

    String role = (user instanceof Tourist) ? "participants" : "admins";
    cometChatService.addMemberToGroup(chatSession.getCometchatGuid(), user.getCometchatUID(), role);
  }

  @Override
  @Transactional
  public void removeUserFromChat(UUID tourInstanceId, UUID userId) {
    removeUsersFromChat(tourInstanceId, List.of(userId));
  }

  @Override
  @Transactional
  public void removeUsersFromChat(UUID tourInstanceId, List<UUID> userIds) {
    chatSessionRepository
        .findByTourInstanceId(tourInstanceId)
        .ifPresent(
            chatSession -> {
              List<User> users = userRepository.findAllById(userIds);
              for (User user : users) {
                if (user.getCometchatUID() != null) {
                  cometChatService.removeMemberFromGroup(
                      chatSession.getCometchatGuid(), user.getCometchatUID());
                }
              }
            });
  }

  @Override
  @Transactional
  public void requestCloseConsultation(UUID sessionId, UUID coordinatorId) {
    ChatSession session =
        chatSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Session not found"));

    if (session.getCoordinator() == null
        || !session.getCoordinator().getId().equals(coordinatorId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Only the assigned coordinator can close");
    }

    // In a real app, we might check last message timestamp from CometChat API here.
    // For now, we update status to CLOSE as requested.
    session.setStatus(ChatSessionStatus.CLOSED);
    chatSessionRepository.save(session);
    log.info(
        "Custom tour consultation session {} closed by coordinator {}", sessionId, coordinatorId);
  }

  @Override
  @Transactional
  public void closeInstanceChat(UUID instanceId, UUID coordinatorId) {
    TourInstance instance =
        tourInstanceRepository
            .findById(instanceId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Instance not found"));

    if (instance.getStatus() != TourInstanceStatus.COMPLETED) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Chat can only be closed for COMPLETED tours");
    }

    ChatSession session =
        chatSessionRepository
            .findByTourInstanceId(instanceId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Chat session not found"));

    if (session.getCoordinator() == null
        || !session.getCoordinator().getId().equals(coordinatorId)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "Only the assigned coordinator can close");
    }

    session.setStatus(ChatSessionStatus.CLOSED);
    chatSessionRepository.save(session);
    log.info("Group chat for instance {} closed by coordinator {}", instanceId, coordinatorId);
  }

  private ChatSessionResponse toResponse(ChatSession chatSession) {
    return ChatSessionResponse.builder()
        .id(chatSession.getId())
        .userId(chatSession.getUser() != null ? chatSession.getUser().getId() : null)
        .coordinatorId(
            chatSession.getCoordinator() != null ? chatSession.getCoordinator().getId() : null)
        .tourId(chatSession.getTour() != null ? chatSession.getTour().getId() : null)
        .cometchatGuid(chatSession.getCometchatGuid())
        .status(chatSession.getStatus())
        .build();
  }
}
