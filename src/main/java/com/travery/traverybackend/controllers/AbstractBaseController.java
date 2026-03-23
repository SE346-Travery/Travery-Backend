package com.travery.traverybackend.controllers;

import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractBaseController {
  @Autowired protected ResponseFactory responseFactory;

  /**
   * Use for API that create new resource (Return 201 Created)
   *
   * @return ResponseEntity<SingleResponse<T>>
   */
  protected <T> ResponseEntity<SingleResponse<T>> created(T data, String message) {
    return responseFactory.success(HttpStatus.CREATED, data, message);
  }

  /**
   * Use for API that return data, update resource (Return 200 OK and data)
   *
   * @return ResponseEntity<SingleResponse<T>>
   */
  protected <T> ResponseEntity<SingleResponse<T>> success(T data, String message) {
    return responseFactory.success(data, message);
  }

  /**
   * Use for API that return success message but no data (Ex: Logout, Delete)
   *
   * @return ResponseEntity<SuccessResponse>
   */
  protected ResponseEntity<SuccessResponse> success(String message) {
    return responseFactory.success(message);
  }
}
