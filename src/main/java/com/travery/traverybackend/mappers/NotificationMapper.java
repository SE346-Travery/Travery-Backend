package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.common.NotificationResponse;
import com.travery.traverybackend.entities.common.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {
  NotificationResponse toResponse(Notification notification);
}
