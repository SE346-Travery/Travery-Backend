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
import com.travery.traverybackend.mappers.UserMapper;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.media.MediaService;
import com.travery.traverybackend.services.user.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MediaService mediaService;

    @Override
    @Transactional(readOnly = true)
    public BaseUserProfileResponse getMyProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public BaseUserProfileResponse updateTouristProfile(UUID userId, UpdateTouristProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

        if (!(user instanceof Tourist tourist)) {
            throw new BaseAppException(UserErrorCode.USER_NOT_FOUND); // Or a specific error like INVALID_ROLE
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
    public BaseUserProfileResponse updateAdminProfile(UUID userId, UpdateAdminProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

        if (!(user instanceof Admin admin)) {
            throw new BaseAppException(UserErrorCode.USER_NOT_FOUND);
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseAppException(UserErrorCode.USER_NOT_FOUND));

        // Delete old avatar if exists
        if (user.getAvatarPublicId() != null) {
            mediaService.deleteImage(user.getAvatarPublicId());
        }

        // Upload new avatar
        Map<String, Object> uploadResult = mediaService.uploadImage(file, CloudinaryFolder.USER_AVATARS);
        user.setAvatarUrl((String) uploadResult.get("secure_url"));
        user.setAvatarPublicId((String) uploadResult.get("public_id"));

        userRepository.save(user);

        return userMapper.toResponse(user);
    }
}
