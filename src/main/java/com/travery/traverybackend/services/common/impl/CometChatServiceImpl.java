package com.travery.traverybackend.services.common.impl;

import com.travery.traverybackend.dtos.request.cometchat.CometChatGroupRequest;
import com.travery.traverybackend.services.common.CometChatService;
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
    }
  }

  public void addMemberToGroup(String guid, String uid) {
    String url = getBaseUrl() + "/groups/" + guid + "/members";

    Map<String, Object> request = Map.of("admins", java.util.List.of(uid));

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, getHeaders());

    try {
      restTemplate.postForEntity(url, entity, Map.class);
      log.info("Successfully added user {} to CometChat group {}", uid, guid);
    } catch (Exception e) {
      log.error("Error adding user {} to CometChat group {}", uid, guid, e);
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
    }
  }

  public void createUser(String uid, String name) {
    String url = getBaseUrl() + "/users";

    Map<String, String> request = Map.of("uid", uid, "name", name);

    HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, getHeaders());

    try {
      restTemplate.postForEntity(url, entity, Map.class);
      log.info("Successfully created CometChat user: {}", uid);
    } catch (Exception e) {
      log.error("Error creating CometChat user: {}", uid, e);
    }
  }
}
