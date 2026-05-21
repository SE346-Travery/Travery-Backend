package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import com.travery.traverybackend.entities.common.ChatSession;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.common.ChatSessionStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.SystemErrorCode;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.repositories.common.ChatSessionRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.ChatSessionService;
import com.travery.traverybackend.services.common.CometChatService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements ChatSessionService {

  private final ChatSessionRepository chatSessionRepository;
  private final TourRepository tourRepository;
  private final TourInstanceRepository tourInstanceRepository;
  private final UserRepository userRepository;
  private final CometChatService cometChatService;

  @Transactional
  public ChatSessionResponse getOrCreateChatSession(UUID tourId) {
    return chatSessionRepository
        .findByTourId(tourId)
        .map(this::toResponse)
        .orElseGet(() -> createChatSession(tourId));
  }

  @Transactional
  public ChatSessionResponse getOrCreateInstanceChatSession(UUID tourInstanceId) {
    return chatSessionRepository
        .findByTourInstanceId(tourInstanceId)
        .map(this::toResponse)
        .orElseGet(() -> createInstanceChatSession(tourInstanceId));
  }

  private ChatSessionResponse createChatSession(UUID tourId) {
    Tour tour =
        tourRepository
            .findById(tourId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour not found"));

    if (!tour.isCustom()) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST,
          "Direct tour chat only available for custom tours. Use instance chat for regular tours.");
    }

    String guid = "custom_tour_" + tour.getId();
    String name = "Chat: " + tour.getName();
    User user = tour.getRequestedByUser();

    if (user == null) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Custom tour must have a requester");
    }

    ensureCometChatUser(tour.getCoordinator());
    ensureCometChatUser(user);

    cometChatService.createGroup(guid, name);

    if (tour.getCoordinator() != null && tour.getCoordinator().getCometchatUID() != null) {
      cometChatService.addMemberToGroup(guid, tour.getCoordinator().getCometchatUID());
    }
    if (user.getCometchatUID() != null) {
      cometChatService.addMemberToGroup(guid, user.getCometchatUID());
    }

    ChatSession chatSession =
        ChatSession.builder()
            .user(user)
            .coordinator(tour.getCoordinator())
            .tour(tour)
            .cometchatGuid(guid)
            .status(ChatSessionStatus.OPEN)
            .build();

    chatSession = chatSessionRepository.save(chatSession);
    return toResponse(chatSession);
  }

  private ChatSessionResponse createInstanceChatSession(UUID tourInstanceId) {
    TourInstance instance =
        tourInstanceRepository
            .findById(tourInstanceId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour instance not found"));

    String guid = "tour_instance_" + instance.getId();
    String name = "Group: " + instance.getTour().getName();

    // Guide should be the primary user/admin for group chats
    User guide = instance.getGuide();
    if (guide == null) {
      // Fallback to coordinator if no guide assigned yet
      guide = instance.getCoordinator();
    }

    ensureCometChatUser(guide);
    ensureCometChatUser(instance.getCoordinator());

    cometChatService.createGroup(guid, name);

    if (guide != null && guide.getCometchatUID() != null) {
      cometChatService.addMemberToGroup(guid, guide.getCometchatUID());
    }
    if (instance.getCoordinator() != null && instance.getCoordinator().getCometchatUID() != null) {
      cometChatService.addMemberToGroup(guid, instance.getCoordinator().getCometchatUID());
    }

    ChatSession chatSession =
        ChatSession.builder()
            .user(guide)
            .coordinator(instance.getCoordinator())
            .tourInstance(instance)
            .tour(instance.getTour())
            .cometchatGuid(guid)
            .status(ChatSessionStatus.OPEN)
            .build();

    chatSession = chatSessionRepository.save(chatSession);
    return toResponse(chatSession);
  }

  @Transactional
  public void addUserToChat(UUID tourInstanceId, UUID userId) {
    ChatSession chatSession =
        chatSessionRepository
            .findByTourInstanceId(tourInstanceId)
            .orElseGet(() -> createInstanceChatSessionEntityOnly(tourInstanceId));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

    ensureCometChatUser(user);
    cometChatService.addMemberToGroup(chatSession.getCometchatGuid(), user.getCometchatUID());
  }

  @Transactional
  public void removeUserFromChat(UUID tourInstanceId, UUID userId) {
    chatSessionRepository
        .findByTourInstanceId(tourInstanceId)
        .ifPresent(
            chatSession -> {
              userRepository
                  .findById(userId)
                  .ifPresent(
                      user -> {
                        if (user.getCometchatUID() != null) {
                          cometChatService.removeMemberFromGroup(
                              chatSession.getCometchatGuid(), user.getCometchatUID());
                        }
                      });
            });
  }

  private ChatSession createInstanceChatSessionEntityOnly(UUID tourInstanceId) {
    createInstanceChatSession(tourInstanceId);
    return chatSessionRepository
        .findByTourInstanceId(tourInstanceId)
        .orElseThrow(() -> new BaseAppException(SystemErrorCode.INTERNAL_SERVER_ERROR));
  }

  private void ensureCometChatUser(User user) {
    if (user == null) return;
    if (user.getCometchatUID() == null) {
      String uid = "user_" + user.getId().toString();
      cometChatService.createUser(uid, user.getFullName());
      user.setCometchatUID(uid);
      userRepository.save(user);
    }
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
