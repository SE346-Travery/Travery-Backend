package com.travery.traverybackend.repositories.common;

import com.travery.traverybackend.entities.common.ChatSession;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<ChatSession> findByTourId(UUID tourId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<ChatSession> findByTourInstanceId(UUID tourInstanceId);

  Optional<ChatSession> findByCometchatGuid(String cometchatGuid);

  boolean existsByCometchatGuid(String cometchatGuid);
}
