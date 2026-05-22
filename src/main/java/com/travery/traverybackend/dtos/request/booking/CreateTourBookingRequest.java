package com.travery.traverybackend.dtos.request.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTourBookingRequest {

  @NotEmpty(message = "At least one booking member is required")
  @Valid
  private List<BookingMemberRequest> members;

  private String specialRequests;

  @JsonIgnore
  private String ipAddress;
}
