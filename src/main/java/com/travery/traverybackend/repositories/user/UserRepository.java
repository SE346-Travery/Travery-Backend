package com.travery.traverybackend.repositories.user;

import com.travery.traverybackend.entities.user.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u WHERE (:role IS NULL OR u.role = :role) AND (:status IS NULL OR u.status = :status)")
  Page<User> findUsersWithFilters(@Param("role") UserRoles role, @Param("status") UserStatus status, Pageable pageable);
}
