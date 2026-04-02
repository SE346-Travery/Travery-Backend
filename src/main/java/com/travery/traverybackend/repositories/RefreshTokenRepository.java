package com.travery.traverybackend.repositories;

import com.travery.traverybackend.entities.auth.RefreshToken;
import com.travery.traverybackend.entities.user.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(User user);

  @Modifying
  @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId")
  void revokeAllByUserId(@Param("userId") UUID userId);
}
