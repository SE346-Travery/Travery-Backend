package com.travery.traverybackend.exception;

import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.DetailedErrorResponse;
import com.travery.traverybackend.dtos.response.base.ErrorResponse;
import com.travery.traverybackend.exception.error.AuthErrorCode;
import com.travery.traverybackend.exception.error.SystemErrorCode;
import com.travery.traverybackend.exception.error.WebErrorCode;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AppExceptionHandler {
  private final ResponseFactory responseFactory;

  @ExceptionHandler(BaseAppException.class)
  public ResponseEntity<ErrorResponse> handleBaseAppException(
      BaseAppException e, HttpServletRequest request) {
    AppErrorCode errorCode = e.getErrorCode();
    log.error(
        "AppException - code: {}, path: {}, message: {}",
        errorCode.getErrorCode(),
        request.getRequestURI(),
        e.getMessage(),
        e);
    ErrorResponse response =
        responseFactory.createErrorResponse(
            errorCode.getHttpStatus(),
            errorCode.getErrorCode(),
            e.getMessage(),
            request.getRequestURI());
    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
  public ResponseEntity<DetailedErrorResponse> handleValidationException(
      Exception e, HttpServletRequest request) {
    log.warn("Validation Error at path: {}", request.getRequestURI());
    Map<String, String> errors = new HashMap<>();

    BindingResult bindingResult = null;
    if (e instanceof BindException bindException) {
      bindingResult = bindException.getBindingResult();
    }

    if (bindingResult != null) {
      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
      }
    }

    DetailedErrorResponse response =
        responseFactory.createDetailedErrorResponse(
            WebErrorCode.VALIDATION_FAILED.getHttpStatus(),
            WebErrorCode.VALIDATION_FAILED.getErrorCode(),
            WebErrorCode.VALIDATION_FAILED.getMessage(),
            request.getRequestURI(),
            errors);
    return ResponseEntity.status(WebErrorCode.BAD_REQUEST.getHttpStatus()).body(response);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(
      Exception e, HttpServletRequest request) {
    log.warn("Bad Credentials: {} - Path: {}", e.getMessage(), request.getRequestURI());
    return buildFromAppErrorCode(AuthErrorCode.BAD_CREDENTIALS, request.getRequestURI());
  }

  @ExceptionHandler({
    AuthenticationCredentialsNotFoundException.class,
    InsufficientAuthenticationException.class
  })
  public ResponseEntity<ErrorResponse> handleMissingTokenException(
      Exception e, HttpServletRequest request) {
    log.warn("Missing/Insufficient Token: {} - Path: {}", e.getMessage(), request.getRequestURI());
    return buildFromAppErrorCode(WebErrorCode.UNAUTHORIZED, request.getRequestURI());
  }

  @ExceptionHandler(InternalAuthenticationServiceException.class)
  public ResponseEntity<ErrorResponse> handleInternalAuthException(
      Exception e, HttpServletRequest request) {
    log.error("Internal Auth Error: {} - Path: {}", e.getMessage(), request.getRequestURI(), e);
    return buildFromAppErrorCode(SystemErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(
      Exception e, HttpServletRequest request) {
    log.warn("Access Denied: {} - Path: {}", e.getMessage(), request.getRequestURI());
    // Trả về WEB_403 - Forbidden
    return buildFromAppErrorCode(WebErrorCode.FORBIDDEN, request.getRequestURI());
  }

  @ExceptionHandler({
    MalformedJwtException.class,
    SignatureException.class,
    UnsupportedJwtException.class,
    IllegalArgumentException.class
  })
  public ResponseEntity<ErrorResponse> handleJwtException(Exception e, HttpServletRequest request) {
    log.warn("Jwt Exception: {} - Path: {}", e.getMessage(), request.getRequestURI());
    return buildFromAppErrorCode(AuthErrorCode.TOKEN_INVALID, request.getRequestURI());
  }

  @ExceptionHandler({
    HttpRequestMethodNotSupportedException.class,
    HttpMessageNotReadableException.class,
    HttpMediaTypeNotSupportedException.class,
    MissingRequestHeaderException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequestException(
      Exception e, HttpServletRequest request) {
    log.warn("Bad request exception: {} - Path: {}", e.getMessage(), request.getRequestURI());
    return buildFromAppErrorCode(
        WebErrorCode.BAD_REQUEST,
        request.getRequestURI(),
        "Invalid request (" + e.getClass().getSimpleName() + ")");
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(
      Exception e, HttpServletRequest request) {
    log.warn("Not found exception: {} - Path: {}", e.getMessage(), request.getRequestURI());
    return buildFromAppErrorCode(WebErrorCode.NOT_FOUND, request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnknownException(
      Exception e, HttpServletRequest request) {
    log.error("Unknown exception: {} - Path: {}", e.getMessage(), request.getRequestURI());
    return buildFromAppErrorCode(SystemErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
  }

  private ResponseEntity<ErrorResponse> buildFromAppErrorCode(AppErrorCode errorCode, String path) {
    ErrorResponse response =
        responseFactory.createErrorResponse(
            errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage(), path);
    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  private ResponseEntity<ErrorResponse> buildFromAppErrorCode(
      AppErrorCode errorCode, String path, String customMessage) {
    ErrorResponse response =
        responseFactory.createErrorResponse(
            errorCode.getHttpStatus(), errorCode.getErrorCode(), customMessage, path);
    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }
}
