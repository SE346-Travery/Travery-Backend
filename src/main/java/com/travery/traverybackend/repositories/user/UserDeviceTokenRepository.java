package com.travery.traverybackend.repositories.user;

import com.travery.traverybackend.entities.user.UserDeviceToken;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, UUID> {
  Optional<UserDeviceToken> findByFcmToken(String fcmToken);

  List<UserDeviceToken> findAllByEmail(String email);

  void deleteByFcmToken(String fcmToken);

  void deleteByEmailAndFcmToken(String email, String fcmToken);
}
