package com.travery.traverybackend.services.user;

import com.travery.traverybackend.dtos.request.profile.UpdateAdminProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateTouristProfileRequest;
import com.travery.traverybackend.dtos.response.profile.BaseUserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserProfileService {
    BaseUserProfileResponse getMyProfile(UUID userId);

    BaseUserProfileResponse updateTouristProfile(UUID userId, UpdateTouristProfileRequest request);

    BaseUserProfileResponse updateAdminProfile(UUID userId, UpdateAdminProfileRequest request);

    BaseUserProfileResponse updateAvatar(UUID userId, MultipartFile file);
}
