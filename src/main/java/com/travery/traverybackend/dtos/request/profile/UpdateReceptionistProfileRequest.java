package com.travery.traverybackend.dtos.request.profile;

import com.travery.traverybackend.enums.user.ShiftType;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReceptionistProfileRequest {
  private String fullName;

  @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number is invalid")
  private String phoneNumber;

  private ShiftType shiftType;

  private UUID hotelId;
}
