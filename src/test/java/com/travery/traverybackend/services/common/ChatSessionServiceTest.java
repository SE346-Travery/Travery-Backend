package com.travery.traverybackend.services.common;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.dtos.response.common.ChatSessionResponse;
import com.travery.traverybackend.entities.common.ChatSession;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.common.ChatSessionStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.repositories.common.ChatSessionRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.impl.ChatSessionServiceImpl;
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
  @Mock private TourRepository tourRepository;
  @Mock private UserRepository userRepository;
  @Mock private CometChatService cometChatService;

  @InjectMocks private ChatSessionServiceImpl chatSessionService;

  private UUID tourId;
  private Tour tour;
  private Coordinator coordinator;
  private User tourist;

  @BeforeEach
  void setUp() {
    tourId = UUID.randomUUID();
    coordinator =
        Coordinator.builder()
            .id(UUID.randomUUID())
            .fullName("Coord")
            .cometchatUID("coord_uid")
            .build();
    tourist =
        User.builder()
            .id(UUID.randomUUID())
            .fullName("Tourist")
            .cometchatUID("tourist_uid")
            .build();
    tour =
        Tour.builder()
            .id(tourId)
            .name("Cool Tour")
            .coordinator(coordinator)
            .isCustom(false)
            .build();
  }

  @Test
  void getOrCreateChatSession_Existing_ReturnsExisting() {
    ChatSession existing =
        ChatSession.builder()
            .id(UUID.randomUUID())
            .tour(tour)
            .user(tourist)
            .cometchatGuid("guid123")
            .status(ChatSessionStatus.OPEN)
            .build();

    when(chatSessionRepository.findByTourId(tourId)).thenReturn(Optional.of(existing));

    ChatSessionResponse response = chatSessionService.getOrCreateChatSession(tourId);

    assertNotNull(response);
    assertEquals("guid123", response.getCometchatGuid());
    verify(chatSessionRepository, never()).save(any());
  }

  @Test
  void getOrCreateChatSession_NewRegularTour_ThrowsException() {
    when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));

    BaseAppException exception =
        assertThrows(
            BaseAppException.class, () -> chatSessionService.getOrCreateChatSession(tourId));

    assertEquals(WebErrorCode.BAD_REQUEST, exception.getErrorCode());
    assertEquals(
        "Direct tour chat only available for custom tours. Use instance chat for regular tours.",
        exception.getMessage());
  }

  @Test
  void getOrCreateChatSession_NewCustomTour_CreatesGroup() {
    tour.setCustom(true);
    tour.setRequestedByUser(tourist);

    when(chatSessionRepository.findByTourId(tourId)).thenReturn(Optional.empty());
    when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
    when(chatSessionRepository.save(any(ChatSession.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ChatSessionResponse response = chatSessionService.getOrCreateChatSession(tourId);

    assertNotNull(response);
    assertTrue(response.getCometchatGuid().startsWith("custom_tour_"));
    verify(cometChatService).createGroup(anyString(), anyString());
  }
}
