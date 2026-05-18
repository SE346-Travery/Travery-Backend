package com.travery.traverybackend.dtos.request.tour;

import com.travery.traverybackend.enums.booking.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
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
public class MemberAttendance {
  @NotNull(message = "Member ID must not be null")
  private UUID memberId;

  @NotNull(message = "Attendance status must not be null")
  private AttendanceStatus status;
}
