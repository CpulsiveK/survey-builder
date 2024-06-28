package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteAdminDto {
    @Email(message = "invalid email")
    @NotEmpty(message = "email field must not be empty")
    @NotNull(message = "email field must not be null")
    private String email;
}
