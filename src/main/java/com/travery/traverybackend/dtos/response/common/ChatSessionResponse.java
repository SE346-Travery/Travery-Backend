package com.travery.traverybackend.dtos.response.common;

import com.travery.traverybackend.enums.common.ChatSessionStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionResponse {
  private UUID id;
  private UUID userId;
  private UUID coordinatorId;
  private UUID tourId;
  private String cometchatGuid;
  private ChatSessionStatus status;
}
