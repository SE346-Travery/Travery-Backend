package com.travery.traverybackend.entities.common;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.common.ChatSessionStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatSession extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coordinator_id")
  private Coordinator coordinator;

  @Column(name = "cometchat_guid", nullable = false, unique = true, length = 100)
  private String cometchatGuid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_id")
  private Tour tour;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private ChatSessionStatus status = ChatSessionStatus.OPEN;
}
