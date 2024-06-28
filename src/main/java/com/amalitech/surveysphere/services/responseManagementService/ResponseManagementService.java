package com.amalitech.surveysphere.services.responseManagementService;

import com.amalitech.surveysphere.dto.requestDto.ResponseManagementDto;
import com.amalitech.surveysphere.dto.responseDto.ResponseAnalysisDto;
import com.amalitech.surveysphere.dto.responseDto.ResponseManagementResponseDto;

/** Interface for managing responses to surveys. */
public interface ResponseManagementService {

  /**
   * Records responses to a survey.
   *
   * @param managementDto The ResponseManagementDto containing response details.
   * @param respondentId The ID of the respondent submitting the responses.
   * @return A ResponseManagementResponseDto representing the result of the response recording
   *     process.
   */
  ResponseManagementResponseDto recordResponses(
      ResponseManagementDto managementDto, String respondentId);

  ResponseAnalysisDto getResponses(String surveyId);
}
