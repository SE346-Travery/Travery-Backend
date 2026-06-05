package com.travery.traverybackend.services.user.impl;

import com.travery.traverybackend.dtos.request.profile.UpdateCoordinatorProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateGuideProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateReceptionistProfileRequest;
import com.travery.traverybackend.dtos.response.profile.BaseUserProfileResponse;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.user.*;
import com.travery.traverybackend.enums.common.CloudinaryFolder;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.UserErrorCode;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.UserMapper;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.CometChatService;
import com.travery.traverybackend.services.common.NotificationService;
import com.travery.traverybackend.services.media.MediaService;
import com.travery.traverybackend.services.user.AdminUserService;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

  private final UserRepository userRepository;
  private final HotelRepository hotelRepository;
  private final UserMapper userMapper;
  private final MediaService mediaService;
  private final NotificationService notificationService;
  private final CometChatService cometChatService;

  @Override
  @Transactional(readOnly = true)
  public Page<BaseUserProfileResponse> getAllUsers(
      UserRoles role, UserStatus status, Pageable pageable) {
    Page<User> users = userRepository.findUsersWithFilters(role, status, pageable);
    return users.map(userMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public BaseUserProfileResponse getUserDetail(UUID targetUserId) {
    User user =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));
    return userMapper.toResponse(user);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse banUser(UUID targetUserId) {
    return changeUserBanStatus(targetUserId, UserStatus.BANNED);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse unbanUser(UUID targetUserId) {
    return changeUserBanStatus(targetUserId, UserStatus.ACTIVE);
  }

  private BaseUserProfileResponse changeUserBanStatus(UUID targetUserId, UserStatus newStatus) {
    User user =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    if (user.getRole() == UserRoles.ADMIN) {
      throw new BaseAppException(UserErrorCode.UNAUTHORIZED_ROLE); // Cannot ban/unban another admin
    }

    if (user.getStatus() == UserStatus.DELETED) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Cannot modify status of a deleted user");
    }

    user.setStatus(newStatus);
    userRepository.save(user);

    // Trigger Notification
    String title = newStatus == UserStatus.BANNED ? "Tài khoản bị khóa" : "Tài khoản đã mở khóa";
    String content =
        newStatus == UserStatus.BANNED
            ? "Tài khoản của bạn đã bị khóa bởi quản trị viên hệ thống."
            : "Tài khoản của bạn đã được mở khóa. Bạn có thể đăng nhập lại ngay bây giờ.";

    notificationService.sendToUser(
        user.getEmail(), NotificationType.SYSTEM_ALERT, title, content, null);

    return userMapper.toResponse(user);
  }

  @Override
  @Transactional
  public void deleteUser(UUID targetUserId) {
    User user =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    if (user.getRole() == UserRoles.ADMIN || user.getRole() == UserRoles.TOURIST) {
      throw new BaseAppException(UserErrorCode.UNAUTHORIZED_ROLE); // Only soft-delete staff
    }

    user.setStatus(UserStatus.DELETED);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse updateAvatar(UUID targetUserId, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "File must not be empty");
    }

    User user =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    if (user.getRole() == UserRoles.ADMIN || user.getRole() == UserRoles.TOURIST) {
      throw new BaseAppException(
          UserErrorCode.UNAUTHORIZED_ROLE); // Admin shouldn't change tourist's avatar
    }

    String oldAvatarPublicId = user.getAvatarPublicId();

    Map<String, Object> uploadResult =
        mediaService.uploadImage(file, CloudinaryFolder.USER_AVATARS);
    user.setAvatarUrl((String) uploadResult.get("secure_url"));
    user.setAvatarPublicId((String) uploadResult.get("public_id"));

    userRepository.save(user);

    // Sync with CometChat
    cometChatService.syncUserAvatar(user.getCometchatUID(), user.getAvatarUrl());

    if (oldAvatarPublicId != null) {
      mediaService.deleteImage(oldAvatarPublicId);
    }
    return userMapper.toResponse(user);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse updateGuideProfile(
      UUID targetUserId, UpdateGuideProfileRequest request) {
    User user =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    if (!(user instanceof Guide guide)) {
      throw new BaseAppException(UserErrorCode.UNAUTHORIZED_ROLE);
    }

    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      guide.setFullName(request.getFullName());
    }
    if (request.getPhoneNumber() != null) {
      guide.setPhoneNumber(request.getPhoneNumber());
    }
    if (request.getGuideLicense() != null && !request.getGuideLicense().isBlank()) {
      guide.setGuideLicense(request.getGuideLicense());
    }
    if (request.getYearsExperience() != null) {
      guide.setYearsExperience(request.getYearsExperience());
    }
    if (request.getLanguages() != null) {
      guide.setLanguages(request.getLanguages());
    }

    userRepository.save(guide);
    return userMapper.toResponse(guide);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse updateCoordinatorProfile(
      UUID targetUserId, UpdateCoordinatorProfileRequest request) {
    User user =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    if (!(user instanceof Coordinator coordinator)) {
      throw new BaseAppException(UserErrorCode.UNAUTHORIZED_ROLE);
    }

    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      coordinator.setFullName(request.getFullName());
    }
    if (request.getPhoneNumber() != null) {
      coordinator.setPhoneNumber(request.getPhoneNumber());
    }
    if (request.getDepartment() != null) {
      coordinator.setDepartment(request.getDepartment());
    }

    userRepository.save(coordinator);
    return userMapper.toResponse(coordinator);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse updateReceptionistProfile(
      UUID targetUserId, UpdateReceptionistProfileRequest request) {
    User user =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    if (!(user instanceof Receptionist receptionist)) {
      throw new BaseAppException(UserErrorCode.UNAUTHORIZED_ROLE);
    }

    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      receptionist.setFullName(request.getFullName());
    }
    if (request.getPhoneNumber() != null) {
      receptionist.setPhoneNumber(request.getPhoneNumber());
    }
    if (request.getShiftType() != null) {
      receptionist.setShiftType(request.getShiftType());
    }
    if (request.getHotelId() != null) {
      Hotel hotel =
          hotelRepository
              .findById(request.getHotelId())
              .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND));
      receptionist.setHotel(hotel);
    }

    userRepository.save(receptionist);
    return userMapper.toResponse(receptionist);
  }
}
