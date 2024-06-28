package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginDto {
  private String name;
  private String username;

  @Email(message = "invalid email")
  private String email;

  private String password;
  private String socialProvider;
}
