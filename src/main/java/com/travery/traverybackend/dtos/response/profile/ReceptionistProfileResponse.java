package com.travery.traverybackend.dtos.response.profile;

import com.travery.traverybackend.enums.user.ShiftType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionistProfileResponse extends BaseUserProfileResponse {
  private String employeeCode;
  private ShiftType shiftType;
  private UUID hotelId;
  private String hotelName;
}
