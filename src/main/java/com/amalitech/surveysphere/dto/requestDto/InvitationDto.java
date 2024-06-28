package com.amalitech.surveysphere.dto.requestDto;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationDto {

  @NotEmpty(message = "email field must not be empty")
  @NotNull(message = "email field must not be null")
  private Map<String, Boolean> emailEditMap;

  private String sender;

  @NotEmpty(message = "Survey must be saved")
  @NotNull(message = "Survey must be saved")
  private String surveyId;
}
