package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.common.ReviewResponse;
import com.travery.traverybackend.entities.common.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true), unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ReviewMapper {

  @Mapping(source = "averageRating", target = "rating")
  @Mapping(source = "user.fullName", target = "reviewerName")
  ReviewResponse toReviewResponse(Review review);
}
