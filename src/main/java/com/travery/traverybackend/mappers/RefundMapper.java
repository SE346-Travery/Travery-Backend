package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.finance.RefundRequestResponse;
import com.travery.traverybackend.entities.finance.RefundRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface RefundMapper {
  
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.fullName", target = "userName")
  @Mapping(source = "user.email", target = "userEmail")
  @Mapping(source = "paymentTransaction.bookingType", target = "bookingType")
  RefundRequestResponse toRefundRequestResponse(RefundRequest entity);
}
