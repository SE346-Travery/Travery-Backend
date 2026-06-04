package com.travery.traverybackend.exception.error;

import com.travery.traverybackend.exception.AppErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SystemErrorCode implements AppErrorCode {
  INTERNAL_SERVER_ERROR("SYS_100", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
  RESOURCE_NOT_FOUND("SYS_101", "Resource not found", HttpStatus.NOT_FOUND),
  ACCESS_DENIED("SYS_102", "Access denied", HttpStatus.FORBIDDEN);

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;
}
