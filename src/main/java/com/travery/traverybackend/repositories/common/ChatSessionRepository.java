package com.travery.traverybackend.repositories.common;

import com.travery.traverybackend.entities.common.ChatSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
  Optional<ChatSession> findByTourId(UUID tourId);

  Optional<ChatSession> findByTourInstanceId(UUID tourInstanceId);

  Optional<ChatSession> findByCometchatGuid(String cometchatGuid);

  boolean existsByCometchatGuid(String cometchatGuid);
}
