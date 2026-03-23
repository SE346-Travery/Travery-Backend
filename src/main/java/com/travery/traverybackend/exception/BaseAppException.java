package com.travery.traverybackend.exception;

import lombok.Getter;

@Getter
public class BaseAppException extends RuntimeException {
  private final AppErrorCode errorCode;

  public BaseAppException(AppErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BaseAppException(AppErrorCode errorCode, Object... args) {
    super(String.format(errorCode.getMessage(), args));
    this.errorCode = errorCode;
  }
}
