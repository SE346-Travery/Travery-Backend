package com.travery.traverybackend.dtos.request.tour;

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
public class GuideAttendanceRequest {

  @NotEmpty(message = "Attendance list must not be empty")
  @Valid
  private List<MemberAttendance> attendances;
}
