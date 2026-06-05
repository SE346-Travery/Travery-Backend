package com.travery.traverybackend.services.common;

import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import java.util.List;
import java.util.UUID;

public interface ChatSessionService {
  ChatSessionResponse initiateCustomTourChat(UUID userId);

  ChatSessionResponse getOrCreateInstanceChatSession(UUID tourInstanceId);

  void addUserToChat(UUID tourInstanceId, UUID userId);

  void removeUserFromChat(UUID tourInstanceId, UUID userId);

  void removeUsersFromChat(UUID tourInstanceId, List<UUID> userIds);

  void requestCloseConsultation(UUID sessionId, UUID coordinatorId);

  void closeInstanceChat(UUID instanceId, UUID coordinatorId);
}
