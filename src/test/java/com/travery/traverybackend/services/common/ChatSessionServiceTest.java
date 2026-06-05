package com.travery.traverybackend.services.common;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import com.travery.traverybackend.entities.common.ChatSession;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.common.ChatSessionRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.impl.ChatSessionServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceTest {

  @Mock private ChatSessionRepository chatSessionRepository;
  @Mock private TourInstanceRepository tourInstanceRepository;
  @Mock private TourBookingRepository tourBookingRepository;
  @Mock private UserRepository userRepository;
  @Mock private CometChatService cometChatService;
  @Mock private NotificationService notificationService;

  @InjectMocks private ChatSessionServiceImpl chatSessionService;

  private UUID userId;
  private User tourist;
  private Coordinator coordinator;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    tourist = User.builder().id(userId).fullName("Tourist").cometchatUID("user_" + userId).build();
    coordinator =
        Coordinator.builder()
            .id(UUID.randomUUID())
            .fullName("Coord")
            .email("coord@test.com")
            .cometchatUID("coord_uid")
            .build();
  }

  @Test
  void initiateCustomTourChat_Valid_AssignsCoordinatorAndNotifies() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(tourist));
    when(userRepository.findAllActiveCoordinators()).thenReturn(List.of(coordinator));
    when(chatSessionRepository.save(any(ChatSession.class)))
        .thenAnswer(
            invocation -> {
              ChatSession session = invocation.getArgument(0);
              session.setId(UUID.randomUUID());
              return session;
            });

    ChatSessionResponse response = chatSessionService.initiateCustomTourChat(userId);

    assertNotNull(response);
    verify(cometChatService).createGroup(anyString(), anyString());
    verify(notificationService)
        .sendToUser(eq(coordinator.getEmail()), any(), anyString(), anyString(), any());
  }
}
