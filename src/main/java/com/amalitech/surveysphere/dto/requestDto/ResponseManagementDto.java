package com.amalitech.surveysphere.dto.requestDto;

import com.amalitech.surveysphere.models.Block;
import com.amalitech.surveysphere.models.Response;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ResponseManagementDto {
  private String email;

  @NotNull(message = "surveyId must not be null")
  @NotEmpty(message = "surveyId must not be empty")
  private String surveyId;

  private List<Response> responses;
}
