package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
  private String name;
  private String username;
  private String role;

  @Email(message = "invalid email")
  @NotEmpty(message = "email field must not be empty")
  @NotNull(message = "email field must not be null")
  private String email;

  @NotEmpty(message = "password field must not be empty")
  @NotNull(message = "password field must not be null")
  @Size(min = 8, message = "password must be 8 characters and above")
  private String password;
}
