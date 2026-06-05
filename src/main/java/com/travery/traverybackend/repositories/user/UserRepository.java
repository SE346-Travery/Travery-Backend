package com.travery.traverybackend.repositories.user;

import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
  Optional<User> findByEmail(String email);

  List<User> findByEmailIn(List<String> emails);

  Optional<User> findByCometchatUID(String cometchatUID);

  @Query(
      "SELECT u FROM User u WHERE u.role = 'COORDINATOR' AND u.status = 'ACTIVE' ORDER BY u.createdAt ASC")
  List<User> findAllActiveCoordinators();

  @Query(
      "SELECT u FROM User u WHERE (:role IS NULL OR u.role = :role) AND (:status IS NULL OR u.status = :status)")
  Page<User> findUsersWithFilters(
      @Param("role") UserRoles role, @Param("status") UserStatus status, Pageable pageable);

  @Query("SELECT g FROM Guide g WHERE g.status = :status")
  List<Guide> findAllGuidesByStatus(@Param("status") UserStatus status);

  @Query(
      "SELECT g FROM Guide g WHERE g.id = :id AND g.status = com.travery.traverybackend.enums.user.UserStatus.ACTIVE")
  Optional<Guide> findActiveGuideById(@Param("id") UUID id);
}
