package com.travery.traverybackend.services.common;

public interface CometChatService {
  void createGroup(String guid, String name);

  void deleteGroup(String guid);

  void addMemberToGroup(String guid, String uid, String role);

  void removeMemberFromGroup(String guid, String uid);

  void createUser(String uid, String name);
}
