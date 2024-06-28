package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserDto {
  FileUploadDto profilePicture;
  @NotBlank(message = "userId field must not be empty")
  String userId;
  String email;
  String username;
}
