package com.travery.traverybackend.controllers.common;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.common.ChatSessionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController extends AbstractBaseController {

  private final ChatSessionService chatSessionService;

  @PostMapping("/initiate")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<ChatSessionResponse>> initiateChat() {
    ChatSessionResponse response = chatSessionService.initiateCustomTourChat(getCurrentUserId());
    return success(response, "Custom tour consultation initiated successfully");
  }

  @PostMapping("/initiate-group")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SingleResponse<ChatSessionResponse>> initiateInstanceChat(
      @RequestParam UUID tourInstanceId) {
    ChatSessionResponse response =
        chatSessionService.getOrCreateInstanceChatSession(tourInstanceId);
    return success(response, "Group chat session initiated successfully");
  }

  @PostMapping("/{id}/request-close")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<Void>> requestCloseConsultation(@PathVariable UUID id) {
    chatSessionService.requestCloseConsultation(id, getCurrentUserId());
    return success(null, "Consultation close request sent successfully");
  }

  @PostMapping("/instance/{instanceId}/close")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<Void>> closeInstanceChat(@PathVariable UUID instanceId) {
    chatSessionService.closeInstanceChat(instanceId, getCurrentUserId());
    return success(null, "Group chat closed successfully");
  }

  private UUID getCurrentUserId() {
    return ((CustomUserDetails)
            SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .getUserId();
  }
}
