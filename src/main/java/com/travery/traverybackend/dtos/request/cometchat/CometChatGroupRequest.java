package com.travery.traverybackend.dtos.request.cometchat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CometChatGroupRequest {
  private String guid;
  private String name;
  private String description;
  @Builder.Default
  private String type = "public";
  private Object metadata;
}
