package com.travery.traverybackend.controllers.common;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import com.travery.traverybackend.services.common.ChatSessionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController extends AbstractBaseController {

  private final ChatSessionService chatSessionService;

  @PostMapping("/initiate")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SingleResponse<ChatSessionResponse>> initiateChat(
      @RequestParam UUID tourId) {
    ChatSessionResponse response = chatSessionService.getOrCreateChatSession(tourId);
    return success(response, "Chat session initiated successfully");
  }

  @PostMapping("/initiate-group")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SingleResponse<ChatSessionResponse>> initiateInstanceChat(
      @RequestParam UUID tourInstanceId) {
    ChatSessionResponse response =
        chatSessionService.getOrCreateInstanceChatSession(tourInstanceId);
    return success(response, "Group chat session initiated successfully");
  }
}
