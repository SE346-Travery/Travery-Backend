package com.travery.traverybackend.exception;

import org.springframework.http.HttpStatus;

public interface AppErrorCode {
  String getErrorCode();

  String getMessage();

  HttpStatus getHttpStatus();
}
