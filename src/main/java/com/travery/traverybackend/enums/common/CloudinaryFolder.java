package com.travery.traverybackend.enums.common;

public enum CloudinaryFolder {
  TOURS("tours"),
  ITINERARIES("itineraries"),
  USER_AVATARS("avatars/users"),
  DRIVER_AVATARS("avatars/drivers"),
  HOTELS("hotels"),
  ROOM_TYPES("room_types"),
  GENERAL("general");

  private final String path;

  CloudinaryFolder(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
