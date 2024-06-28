package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArchiveDto {
  @NotBlank(message = "ID cannot be blank")
  String surveyId;
}
