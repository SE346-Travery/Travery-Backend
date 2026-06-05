package com.travery.traverybackend.repositories.common;

import com.travery.traverybackend.entities.common.Notification;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  Page<Notification> findAllByUserEmailOrderByCreatedAtDesc(String email, Pageable pageable);

  long countByUserEmailAndIsReadFalse(String email);

  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.email = :email")
  void markAllAsRead(String email);

  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.user.email = :email")
  int markAsRead(UUID id, String email);

  void deleteByIdAndUserEmail(UUID id, String email);
}
