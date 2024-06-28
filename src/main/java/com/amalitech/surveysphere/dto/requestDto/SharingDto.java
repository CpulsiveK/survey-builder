package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharingDto {
  @NotEmpty(message = "emails field must not be empty")
  @Email(message = "invalid email")
  List<String> emails;

  String subject;
  String message;

  @NotBlank(message = "surveyId field must not be empty")
  String surveyId;
}
