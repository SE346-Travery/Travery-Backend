package com.travery.traverybackend.controllers.user;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.profile.UpdateAdminProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateTouristProfileRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.profile.BaseUserProfileResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.user.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController extends AbstractBaseController {

  private final UserProfileService userProfileService;

  @GetMapping("/me")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> getMyProfile(
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    return success(
        userProfileService.getMyProfile(currentUser.getUserId()), "Profile retrieved successfully");
  }

  @PatchMapping("/tourist/me")
  @PreAuthorize("hasRole('TOURIST')")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> updateTouristProfile(
      @AuthenticationPrincipal CustomUserDetails currentUser,
      @Valid @RequestBody UpdateTouristProfileRequest request) {
    return success(
        userProfileService.updateTouristProfile(currentUser.getUserId(), request),
        "Tourist profile updated successfully");
  }

  @PatchMapping("/admin/me")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> updateAdminProfile(
      @AuthenticationPrincipal CustomUserDetails currentUser,
      @Valid @RequestBody UpdateAdminProfileRequest request) {
    return success(
        userProfileService.updateAdminProfile(currentUser.getUserId(), request),
        "Admin profile updated successfully");
  }

  @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ADMIN', 'TOURIST')")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> updateAvatar(
      @AuthenticationPrincipal CustomUserDetails currentUser,
      @RequestParam("file") MultipartFile file) {
    return success(
        userProfileService.updateAvatar(currentUser.getUserId(), file),
        "Avatar updated successfully");
  }
}
