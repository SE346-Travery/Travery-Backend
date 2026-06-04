package com.travery.traverybackend.services.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.dtos.response.profile.BaseUserProfileResponse;
import com.travery.traverybackend.entities.user.Admin;
import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.UserErrorCode;
import com.travery.traverybackend.mappers.UserMapper;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.media.MediaService;
import com.travery.traverybackend.services.common.NotificationService;
import com.travery.traverybackend.services.user.impl.AdminUserServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private HotelRepository hotelRepository;

  @Mock private UserMapper userMapper;

  @Mock private MediaService mediaService;

  @Mock private NotificationService notificationService;

  @InjectMocks private AdminUserServiceImpl adminUserService;

  private Guide guide;
  private Admin admin;
  private Tourist tourist;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    guide =
        Guide.builder()
            .id(userId)
            .fullName("Test Guide")
            .email("guide@example.com")
            .role(UserRoles.GUIDE)
            .status(UserStatus.ACTIVE)
            .build();

    admin = Admin.builder().id(UUID.randomUUID()).role(UserRoles.ADMIN).build();

    tourist = Tourist.builder().id(UUID.randomUUID()).role(UserRoles.TOURIST).build();
  }

  @Test
  void getAllUsers_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<com.travery.traverybackend.entities.user.User> users = new PageImpl<>(List.of(guide));

    when(userRepository.findUsersWithFilters(UserRoles.GUIDE, UserStatus.ACTIVE, pageable))
        .thenReturn(users);
    when(userMapper.toResponse(any())).thenReturn(new BaseUserProfileResponse());

    Page<BaseUserProfileResponse> response =
        adminUserService.getAllUsers(UserRoles.GUIDE, UserStatus.ACTIVE, pageable);

    assertThat(response.getContent()).hasSize(1);
  }

  @Test
  void banUser_Success() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(guide));
    when(userMapper.toResponse(any())).thenReturn(new BaseUserProfileResponse());

    adminUserService.banUser(userId);

    assertThat(guide.getStatus()).isEqualTo(UserStatus.BANNED);
    verify(userRepository).save(guide);
  }

  @Test
  void unbanUser_Success() {
    guide.setStatus(UserStatus.BANNED);
    when(userRepository.findById(userId)).thenReturn(Optional.of(guide));
    when(userMapper.toResponse(any())).thenReturn(new BaseUserProfileResponse());

    adminUserService.unbanUser(userId);

    assertThat(guide.getStatus()).isEqualTo(UserStatus.ACTIVE);
    verify(userRepository).save(guide);
  }

  @Test
  void banUser_TargetIsAdmin_ThrowsException() {
    when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

    BaseAppException exception =
        assertThrows(BaseAppException.class, () -> adminUserService.banUser(admin.getId()));

    assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.UNAUTHORIZED_ROLE);
  }

  @Test
  void deleteUser_Success() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(guide));

    adminUserService.deleteUser(userId);

    assertThat(guide.getStatus()).isEqualTo(UserStatus.DELETED);
    verify(userRepository).save(guide);
  }

  @Test
  void deleteUser_TargetIsTourist_ThrowsException() {
    when(userRepository.findById(tourist.getId())).thenReturn(Optional.of(tourist));

    BaseAppException exception =
        assertThrows(BaseAppException.class, () -> adminUserService.deleteUser(tourist.getId()));

    assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.UNAUTHORIZED_ROLE);
  }
}
