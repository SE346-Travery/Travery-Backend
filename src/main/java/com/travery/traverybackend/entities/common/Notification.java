package com.travery.traverybackend.entities.common;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.common.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "notifications",
    indexes = {
      @Index(name = "idx_notification_user_email", columnList = "user_email"),
      @Index(name = "idx_notification_is_read", columnList = "is_read")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Notification extends AbstractBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "user_email",
      referencedColumnName = "email",
      foreignKey = @ForeignKey(name = "fk_notification_user"))
  private User user;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  private NotificationType type;

  @Column(name = "data_id", length = 100)
  private String dataId;

  @Column(name = "is_read", nullable = false)
  @Builder.Default
  private Boolean isRead = false;
}
