package com.travery.traverybackend.services.user.impl;

import com.travery.traverybackend.dtos.request.profile.UpdateAdminProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateTouristProfileRequest;
import com.travery.traverybackend.dtos.response.profile.BaseUserProfileResponse;
import com.travery.traverybackend.entities.user.Admin;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.common.CloudinaryFolder;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.UserErrorCode;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.UserMapper;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.common.CometChatService;
import com.travery.traverybackend.services.media.MediaService;
import com.travery.traverybackend.services.user.UserProfileService;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final MediaService mediaService;
  private final CometChatService cometChatService;

  @Override
  @Transactional(readOnly = true)
  public BaseUserProfileResponse getMyProfile(UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));
    return userMapper.toResponse(user);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse updateTouristProfile(
      UUID userId, UpdateTouristProfileRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    if (!(user instanceof Tourist tourist)) {
      throw new BaseAppException(UserErrorCode.UNAUTHORIZED_ROLE);
    }

    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      tourist.setFullName(request.getFullName());
    }
    if (request.getPhoneNumber() != null) {
      tourist.setPhoneNumber(request.getPhoneNumber());
    }
    if (request.getPassportNumber() != null) {
      tourist.setPassportNumber(request.getPassportNumber());
    }
    if (request.getDateOfBirth() != null) {
      tourist.setDateOfBirth(request.getDateOfBirth());
    }
    if (request.getGender() != null) {
      tourist.setGender(request.getGender());
    }

    userRepository.save(tourist);

    return userMapper.toResponse(tourist);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse updateAdminProfile(
      UUID userId, UpdateAdminProfileRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    if (!(user instanceof Admin admin)) {
      throw new BaseAppException(UserErrorCode.UNAUTHORIZED_ROLE);
    }

    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      admin.setFullName(request.getFullName());
    }
    if (request.getPhoneNumber() != null) {
      admin.setPhoneNumber(request.getPhoneNumber());
    }

    userRepository.save(admin);

    return userMapper.toResponse(admin);
  }

  @Override
  @Transactional
  public BaseUserProfileResponse updateAvatar(UUID userId, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "File must not be empty");
    }

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

    String oldAvatarPublicId = user.getAvatarPublicId();

    // Upload new avatar first
    Map<String, Object> uploadResult =
        mediaService.uploadImage(file, CloudinaryFolder.USER_AVATARS);
    user.setAvatarUrl((String) uploadResult.get("secure_url"));
    user.setAvatarPublicId((String) uploadResult.get("public_id"));

    userRepository.save(user);

    // Sync with CometChat
    cometChatService.syncUserAvatar(user.getCometchatUID(), user.getAvatarUrl());

    // Delete old avatar only after successful upload and save
    if (oldAvatarPublicId != null) {
      mediaService.deleteImage(oldAvatarPublicId);
    }

    return userMapper.toResponse(user);
  }
}
