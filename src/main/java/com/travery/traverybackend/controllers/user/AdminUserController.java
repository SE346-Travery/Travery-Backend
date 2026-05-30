package com.travery.traverybackend.controllers.user;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.profile.UpdateCoordinatorProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateGuideProfileRequest;
import com.travery.traverybackend.dtos.request.profile.UpdateReceptionistProfileRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.profile.BaseUserProfileResponse;
import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;
import com.travery.traverybackend.services.user.AdminUserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController extends AbstractBaseController {

  private final AdminUserService adminUserService;

  @GetMapping
  public ResponseEntity<SingleResponse<Page<BaseUserProfileResponse>>> getAllUsers(
      @RequestParam(required = false) UserRoles role,
      @RequestParam(required = false) UserStatus status,
      Pageable pageable) {
    return success(
        adminUserService.getAllUsers(role, status, pageable), "Users fetched successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> getUserDetail(
      @PathVariable UUID id) {
    return success(adminUserService.getUserDetail(id), "User detail fetched successfully");
  }

  @PatchMapping("/{id}/ban")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> banUser(@PathVariable UUID id) {
    return success(adminUserService.banUser(id), "User banned successfully");
  }

  @PatchMapping("/{id}/unban")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> unbanUser(@PathVariable UUID id) {
    return success(adminUserService.unbanUser(id), "User unbanned successfully");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<SuccessResponse> deleteUser(@PathVariable UUID id) {
    adminUserService.deleteUser(id);
    return success("User deleted (soft delete) successfully");
  }

  @PutMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> updateAvatar(
      @PathVariable UUID id, @RequestParam("file") MultipartFile file) {
    return success(adminUserService.updateAvatar(id, file), "Staff avatar updated successfully");
  }

  @PatchMapping("/guides/{id}")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> updateGuideProfile(
      @PathVariable UUID id, @Valid @RequestBody UpdateGuideProfileRequest request) {
    return success(
        adminUserService.updateGuideProfile(id, request), "Guide profile updated successfully");
  }

  @PatchMapping("/coordinators/{id}")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> updateCoordinatorProfile(
      @PathVariable UUID id, @Valid @RequestBody UpdateCoordinatorProfileRequest request) {
    return success(
        adminUserService.updateCoordinatorProfile(id, request),
        "Coordinator profile updated successfully");
  }

  @PatchMapping("/receptionists/{id}")
  public ResponseEntity<SingleResponse<BaseUserProfileResponse>> updateReceptionistProfile(
      @PathVariable UUID id, @Valid @RequestBody UpdateReceptionistProfileRequest request) {
    return success(
        adminUserService.updateReceptionistProfile(id, request),
        "Receptionist profile updated successfully");
  }
}
