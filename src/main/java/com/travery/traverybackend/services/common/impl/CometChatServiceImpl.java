package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.request.cometchat.CometChatGroupRequest;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.SystemErrorCode;
import com.travery.traverybackend.services.common.CometChatService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CometChatServiceImpl implements CometChatService {

  private final RestTemplate restTemplate;

  @Value("${cometchat.app-id}")
  private String appId;

  @Value("${cometchat.api-key}")
  private String apiKey;

  @Value("${cometchat.region}")
  private String region;

  private String getBaseUrl() {
    return String.format("https://%s.api-%s.cometchat.io/v3", appId, region);
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("apiKey", apiKey);
    headers.set("accept", "application/json");
    return headers;
  }

  public void createGroup(String guid, String name) {
    String url = getBaseUrl() + "/groups";

    CometChatGroupRequest request = CometChatGroupRequest.builder().guid(guid).name(name).build();

    HttpEntity<CometChatGroupRequest> entity = new HttpEntity<>(request, getHeaders());

    try {
      restTemplate.postForEntity(url, entity, Map.class);
      log.info("Successfully created CometChat group: {}", guid);
    } catch (Exception e) {
      log.error("Error creating CometChat group: {}", guid, e);
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to create chat group");
    }
  }

  public void deleteGroup(String guid) {
    String url = getBaseUrl() + "/groups/" + guid;

    HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

    try {
      restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);
      log.info("Successfully deleted CometChat group: {}", guid);
    } catch (Exception e) {
      log.error("Error deleting CometChat group: {}", guid, e);
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to delete chat group");
    }
  }

  public void addMemberToGroup(String guid, String uid, String role) {
    String url = getBaseUrl() + "/groups/" + guid + "/members";

    Map<String, Object> request = Map.of(role, java.util.List.of(uid));

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, getHeaders());

    try {
      restTemplate.postForEntity(url, entity, Map.class);
      log.info("Successfully added user {} to CometChat group {} as {}", uid, guid, role);
    } catch (Exception e) {
      log.error("Error adding user {} to CometChat group {}", uid, guid, e);
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to add member to chat group");
    }
  }

  public void removeMemberFromGroup(String guid, String uid) {
    String url = getBaseUrl() + "/groups/" + guid + "/members/" + uid;

    HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

    try {
      restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);
      log.info("Successfully removed user {} from CometChat group {}", uid, guid);
    } catch (Exception e) {
      log.error("Error removing user {} from CometChat group {}", uid, guid, e);
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to remove member from chat group");
    }
  }

  @Override
  public void createUser(String uid, String name, String avatarUrl) {
    String url = getBaseUrl() + "/users";

    Map<String, String> request = new HashMap<>();
    request.put("uid", uid);
    request.put("name", name);
    if (avatarUrl != null) {
      request.put("avatar", avatarUrl);
    }

    HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, getHeaders());

    try {
      restTemplate.postForEntity(url, entity, Map.class);
      log.info("Successfully created CometChat user: {}", uid);
    } catch (Exception e) {
      log.error("Error creating CometChat user: {}", uid, e);
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to create chat user");
    }
  }

  @Override
  public void registerPushToken(String uid, String fcmToken) {
    String url = getBaseUrl() + "/users/" + uid + "/push-tokens";

    Map<String, String> request = Map.of("fcm", fcmToken);

    HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, getHeaders());

    try {
      restTemplate.postForEntity(url, entity, Map.class);
      log.info("Successfully registered FCM token for CometChat user: {}", uid);
    } catch (Exception e) {
      log.error("Error registering FCM token for CometChat user: {}", uid, e);
      // We don't throw exception here to avoid breaking the main auth flow
    }
  }

  @Override
  public void syncUserAvatar(String uid, String avatarUrl) {
    String url = getBaseUrl() + "/users/" + uid;

    Map<String, String> request = new HashMap<>();
    request.put("avatar", avatarUrl != null ? avatarUrl : "");

    HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, getHeaders());

    try {
      restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
      log.info("Successfully synced avatar for CometChat user: {}", uid);
    } catch (Exception e) {
      log.error("Error syncing avatar for CometChat user: {}", uid, e);
    }
  }
}
