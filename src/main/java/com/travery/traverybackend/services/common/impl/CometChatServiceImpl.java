package com.travery.traverybackend.services.common.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CometChatServiceImpl implements CometChatService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("${cometchat.app-id}")
  private String appId;

  @Value("${cometchat.api-key}")
  private String apiKey;

  @Value("${cometchat.region}")
  private String region;

  private String getBaseUrl() {
    if (appId == null || appId.isBlank() || region == null || region.isBlank()) {
      log.error("CometChat configuration is missing or invalid: appId={}, region={}", appId, region);
    }
    return String.format("https://%s.api-%s.cometchat.io/v3", appId, region);
  }

  private HttpHeaders getHeaders() {
    if (apiKey == null || apiKey.isBlank()) {
      log.error("CometChat API Key is missing or invalid");
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("apiKey", apiKey);
    headers.set("accept", "application/json");
    return headers;
  }
  public void createGroup(String guid, String name) {
    String url = getBaseUrl() + "/groups";

    CometChatGroupRequest request =
        CometChatGroupRequest.builder()
            .guid(guid)
            .name(name)
            .description("Group for " + name)
            .metadata(Map.of("creationTime", System.currentTimeMillis()))
            .build();

    HttpEntity<CometChatGroupRequest> entity = new HttpEntity<>(request, getHeaders());

    try {
      restTemplate.postForEntity(url, entity, Map.class);
      log.info("Successfully created CometChat group: {}", guid);
    } catch (HttpStatusCodeException e) {
      String responseBody = e.getResponseBodyAsString();
      if (responseBody.contains("ERR_GUID_ALREADY_EXISTS")) {
        log.warn("CometChat group already exists: {}", guid);
        return;
      }
      log.error("Error creating CometChat group: {}. Response: {}", guid, responseBody);
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to create chat group: " + e.getMessage());
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
    } catch (HttpStatusCodeException e) {
      log.error(
          "Error deleting CometChat group: {}. Response: {}", guid, e.getResponseBodyAsString());
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to delete chat group");
    } catch (Exception e) {
      log.error("Error deleting CometChat group: {}", guid, e);
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to delete chat group");
    }
  }

  public void addMemberToGroup(String guid, String uid, String role) {
    String url = getBaseUrl() + "/groups/" + guid + "/members";

    Map<String, Object> request = new HashMap<>();
    request.put(role, java.util.List.of(uid));

    String body;
    try {
      body = objectMapper.writeValueAsString(request);
      log.debug("Adding member to group. URL: {}, Payload: {}", url, body);
    } catch (Exception e) {
      log.error("Failed to serialize CometChat request body", e);
      throw new BaseAppException(SystemErrorCode.INTERNAL_SERVER_ERROR, "Internal Error");
    }

    HttpEntity<String> entity = new HttpEntity<>(body, getHeaders());

    try {
      restTemplate.postForEntity(url, entity, Map.class);
      log.info("Successfully added user {} to CometChat group {} as {}", uid, guid, role);
    } catch (HttpStatusCodeException e) {
      log.error(
          "Error adding user {} to CometChat group {}. Response: {}",
          uid,
          guid,
          e.getResponseBodyAsString());
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to add member to chat group");
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
    } catch (HttpStatusCodeException e) {
      log.error(
          "Error removing user {} from CometChat group {}. Response: {}",
          uid,
          guid,
          e.getResponseBodyAsString());
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to remove member from chat group");
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
    } catch (HttpStatusCodeException e) {
      String responseBody = e.getResponseBodyAsString();
      if (responseBody.contains("ERR_UID_ALREADY_EXISTS")) {
        log.warn("CometChat user already exists: {}", uid);
        return;
      }
      log.error("Error creating CometChat user: {}. Response: {}", uid, responseBody);
      throw new BaseAppException(
          SystemErrorCode.INTERNAL_SERVER_ERROR, "Failed to create chat user");
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
    } catch (HttpStatusCodeException e) {
      log.error(
          "Error registering FCM token for CometChat user: {}. Response: {}",
          uid,
          e.getResponseBodyAsString());
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
    } catch (HttpStatusCodeException e) {
      log.error(
          "Error syncing avatar for CometChat user: {}. Response: {}",
          uid,
          e.getResponseBodyAsString());
    } catch (Exception e) {
      log.error("Error syncing avatar for CometChat user: {}", uid, e);
    }
  }
}
