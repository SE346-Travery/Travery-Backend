package com.travery.traverybackend.services.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.travery.traverybackend.dtos.request.profile.UpdateTouristProfileRequest;
import com.travery.traverybackend.dtos.response.profile.BaseUserProfileResponse;
import com.travery.traverybackend.entities.user.Admin;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.enums.common.CloudinaryFolder;
import com.travery.traverybackend.enums.user.Gender;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.UserErrorCode;
import com.travery.traverybackend.mappers.UserMapper;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.media.MediaService;
import com.travery.traverybackend.services.user.impl.UserProfileServiceImpl;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @Mock private MediaService mediaService;

  @InjectMocks private UserProfileServiceImpl userProfileService;

  private Tourist tourist;
  private Admin admin;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    tourist =
        Tourist.builder().id(userId).fullName("John Tourist").email("tourist@test.com").build();

    admin = Admin.builder().id(userId).fullName("Jane Admin").email("admin@test.com").build();
  }

  @Test
  void getMyProfile_Success() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(tourist));
    BaseUserProfileResponse mockResponse = new BaseUserProfileResponse();
    when(userMapper.toResponse(tourist)).thenReturn(mockResponse);

    BaseUserProfileResponse response = userProfileService.getMyProfile(userId);

    assertThat(response).isNotNull();
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  void getMyProfile_UserNotFound() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    BaseAppException exception =
        assertThrows(BaseAppException.class, () -> userProfileService.getMyProfile(userId));

    assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
  }

  @Test
  void updateTouristProfile_Success() {
    UpdateTouristProfileRequest request =
        UpdateTouristProfileRequest.builder()
            .fullName("Updated Tourist")
            .phoneNumber("+123456789")
            .gender(Gender.MALE)
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(tourist));
    BaseUserProfileResponse mockResponse = new BaseUserProfileResponse();
    when(userMapper.toResponse(any(Tourist.class))).thenReturn(mockResponse);

    BaseUserProfileResponse response = userProfileService.updateTouristProfile(userId, request);

    assertThat(response).isNotNull();
    assertThat(tourist.getFullName()).isEqualTo("Updated Tourist");
    verify(userRepository).save(tourist);
  }

  @Test
  void updateTouristProfile_InvalidRole_ThrowsException() {
    UpdateTouristProfileRequest request = new UpdateTouristProfileRequest();
    when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

    BaseAppException exception =
        assertThrows(
            BaseAppException.class, () -> userProfileService.updateTouristProfile(userId, request));

    assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
  }

  @Test
  void updateAvatar_Success_WithOldAvatarDeletion() {
    tourist.setAvatarPublicId("old-public-id");
    MockMultipartFile file =
        new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

    when(userRepository.findById(userId)).thenReturn(Optional.of(tourist));
    when(mediaService.uploadImage(any(MultipartFile.class), eq(CloudinaryFolder.USER_AVATARS)))
        .thenReturn(Map.of("secure_url", "new-url.jpg", "public_id", "new-public-id"));

    BaseUserProfileResponse mockResponse = new BaseUserProfileResponse();
    when(userMapper.toResponse(tourist)).thenReturn(mockResponse);

    BaseUserProfileResponse response = userProfileService.updateAvatar(userId, file);

    assertThat(response).isNotNull();
    verify(mediaService).deleteImage("old-public-id");
    assertThat(tourist.getAvatarUrl()).isEqualTo("new-url.jpg");
    assertThat(tourist.getAvatarPublicId()).isEqualTo("new-public-id");
    verify(userRepository).save(tourist);
  }
}
