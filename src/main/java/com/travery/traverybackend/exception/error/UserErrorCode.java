package com.travery.traverybackend.exception.error;

import com.travery.traverybackend.exception.AppErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements AppErrorCode {
  USER_NOT_FOUND("USER_101", "User with id '%s' not found", HttpStatus.NOT_FOUND),
  USER_EXISTED("USER_102", "Email already registered", HttpStatus.CONFLICT),
  USER_ALREADY_ACTIVE("USER_103", "User is already active", HttpStatus.BAD_REQUEST),
  USER_BANNED("USER_104", "User account is banned", HttpStatus.FORBIDDEN);

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;
}
