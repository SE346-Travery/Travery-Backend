package com.travery.traverybackend.services.user;

import com.travery.traverybackend.dtos.request.profile.UpdateCoordinatorProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateGuideProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateReceptionistProfileRequest;
import com.travery.traverybackend.dtos.response.profile.BaseUserProfileResponse;
import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AdminUserService {
    Page<BaseUserProfileResponse> getAllUsers(UserRoles role, UserStatus status, Pageable pageable);

    BaseUserProfileResponse getUserDetail(UUID targetUserId);

    BaseUserProfileResponse banUser(UUID targetUserId);

    BaseUserProfileResponse unbanUser(UUID targetUserId);

    void deleteUser(UUID targetUserId);

    BaseUserProfileResponse updateAvatar(UUID targetUserId, MultipartFile file);

    BaseUserProfileResponse updateGuideProfile(UUID targetUserId, UpdateGuideProfileRequest request);

    BaseUserProfileResponse updateCoordinatorProfile(UUID targetUserId, UpdateCoordinatorProfileRequest request);

    BaseUserProfileResponse updateReceptionistProfile(UUID targetUserId, UpdateReceptionistProfileRequest request);
}
