package com.travery.traverybackend.services.common;

import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import java.util.UUID;

public interface ChatSessionService {
  ChatSessionResponse getOrCreateChatSession(UUID tourId);

  ChatSessionResponse getOrCreateInstanceChatSession(UUID tourInstanceId);

  void addUserToChat(UUID tourInstanceId, UUID userId);

  void removeUserFromChat(UUID tourInstanceId, UUID userId);
}
