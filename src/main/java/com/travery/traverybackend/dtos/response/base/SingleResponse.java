package com.travery.traverybackend.dtos.response.base;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.travery.traverybackend.dtos.response.AbstractBaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @SuperBuilder
// @JsonPropertyOrder({ "statusCode", "message", "data" })
// public class SingleResponse<T> extends AbstractBaseResponse {
//    private T data;
// }

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonPropertyOrder({"httpStatus", "message", "data"})
public class SingleResponse<T> extends AbstractBaseResponse {
  private T data;
}
