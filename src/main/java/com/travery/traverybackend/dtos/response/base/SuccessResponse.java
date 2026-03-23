package com.travery.traverybackend.dtos.response.base;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.travery.traverybackend.dtos.response.AbstractBaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter // @Getter để Jackson đọc được dữ liệu chuyển thành JSON
@Setter // Có thể bỏ luôn nếu sau khi được tạo ra không thay đổi gì.
@AllArgsConstructor
@SuperBuilder
@JsonPropertyOrder({"httpStatus", "message"})
public class SuccessResponse extends AbstractBaseResponse {}
