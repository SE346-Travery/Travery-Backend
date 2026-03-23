package com.travery.traverybackend.exception.error;

import com.travery.traverybackend.exception.AppErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WebErrorCode implements AppErrorCode {
  BAD_REQUEST("WEB_400", "Bad Request: The request is invalid", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED("WEB_401", "Unauthorized: You are not authenticated", HttpStatus.UNAUTHORIZED),
  FORBIDDEN(
      "WEB_403",
      "Access Denied: You do not have permission to access this resource",
      HttpStatus.FORBIDDEN),
  NOT_FOUND("WEB_404", "Not Found: The requested resource was not found", HttpStatus.NOT_FOUND),
  METHOD_NOT_ALLOWED(
      "WEB_405",
      "Method Not Allowed: The requested method is not supported",
      HttpStatus.METHOD_NOT_ALLOWED),
  VALIDATION_FAILED("WEB_406", "Validation error: Invalid data", HttpStatus.BAD_REQUEST),
  UNSUPPORTED_MEDIA_TYPE(
      "WEB_415",
      "Unsupported Media Type: The requested media type is not supported",
      HttpStatus.UNSUPPORTED_MEDIA_TYPE);

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;
}
