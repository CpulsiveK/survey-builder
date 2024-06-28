package com.amalitech.surveysphere.dto.requestDto;

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
public class SurveySchedulerDto {
  @NotEmpty(message = "scheduleDate must not be empty")
  @NotNull(message = "scheduleDate must not be null")
  private String scheduleDate;

  private SharingDto sharingInfo;
}
