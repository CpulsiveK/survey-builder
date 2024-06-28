package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRoomUserDto {
  @NotBlank(message = "email cannot be blank")
  @Email(message = "invalid email")
  String email;

  String adminId = "Admin";
}
