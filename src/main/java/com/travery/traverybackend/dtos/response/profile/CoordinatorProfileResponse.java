package com.travery.traverybackend.dtos.response.profile;

import com.travery.traverybackend.enums.user.Department;
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
public class CoordinatorProfileResponse extends BaseUserProfileResponse {
    private String employeeCode;
    private Department department;
}
