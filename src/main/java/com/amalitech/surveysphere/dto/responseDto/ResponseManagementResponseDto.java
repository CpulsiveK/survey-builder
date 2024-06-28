package com.amalitech.surveysphere.dto.responseDto;

import com.amalitech.surveysphere.models.Respondent;

import java.util.List;

import com.amalitech.surveysphere.models.Survey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseManagementResponseDto {
  private String respondentId;
  private String message;
  private String time;
}
