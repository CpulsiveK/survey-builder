package com.amalitech.surveysphere.services.surveyService;

import com.amalitech.surveysphere.dto.requestDto.AccountManagementDto;
import com.amalitech.surveysphere.dto.requestDto.SurveyDto;
import com.amalitech.surveysphere.dto.requestDto.SurveySchedulerDto;
import com.amalitech.surveysphere.dto.responseDto.AllSurveysResponseDto;
import com.amalitech.surveysphere.dto.responseDto.DistributionResponseDto;
import com.amalitech.surveysphere.dto.responseDto.ExcelResponseDto;
import com.amalitech.surveysphere.models.ScheduledSurvey;
import com.amalitech.surveysphere.models.Survey;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/** Interface defining operations related to survey management. */
public interface SurveyService {
  /**
   * Creates a new survey.
   *
   * @param surveyDto The SurveyDto object containing survey information.
   * @return Survey.
   */
  Survey createSurvey(SurveyDto surveyDto);

  /**
   * Updates an existing survey.
   *
   * @param surveyDto The SurveyDto object containing updated survey information.
   * @param surveyId The ID of the survey to be updated.
   * @return Survey.
   */
  com.amalitech.surveysphere.models.Survey updateSurvey(SurveyDto surveyDto, String surveyId);

  /**
   * Retrieves all active surveys.
   *
   * @return All active surveys.
   */
  AllSurveysResponseDto getActiveSurveys(int page, int limit, String uuid);

  /**
   * Retrieves all sent active surveys owned by the survey owner.
   *
   * @return A list of all active surveys.
   */
  AllSurveysResponseDto getSentSurveys(int page, int limit);

  /**
   * Retrieves all drafted active surveys owned by the survey owner.
   *
   * @return A list of all active surveys.
   */
  AllSurveysResponseDto getDraftSurveys(int page, int limit);

  /**
   * Retrieves all archived active surveys owned by the survey owner.
   *
   * @return A list of all active surveys.
   */
  AllSurveysResponseDto getArchivedSurveys(int page, int limit);

  /**
   * Retrieves all scheduled active surveys owned by the survey owner.
   *
   * @return A list of all active surveys.
   */
  AllSurveysResponseDto getScheduledSurveys(int page, int limit);

  /**
   * Distributes a survey to specified email addresses.
   *
   * @param email The list of email addresses to which the survey will be distributed.
   * @param subject The subject of the distribution email.
   * @param message The message body of the distribution email.
   * @param surveyId The ID of the survey to be distributed.
   * @return DistributionResponseDto.
   * @throws MessagingException if an error occurs during messaging.
   */
  DistributionResponseDto distributeSurvey(
      List<String> email, String subject, String message, String surveyId)
      throws MessagingException;

  /**
   * Retrieves a survey by its ID.
   *
   * @param surveyId The ID of the survey to retrieve.
   * @return Object representing the retrieved survey.
   */
  com.amalitech.surveysphere.models.Survey getSurvey(String surveyId);

  /**
   * Schedules the distribution of a survey.
   *
   * @param schedulerDto The SurveySchedulerDto object containing survey distribution scheduling
   *     information.
   * @return Scheduled survey for distribution.
   */
  ScheduledSurvey scheduleSurveyDistribution(SurveySchedulerDto schedulerDto);

  /**
   * Schedules the deletion of a survey.
   *
   * @param schedulerDto The SurveySchedulerDto object containing survey deletion scheduling
   *     information.
   * @return Scheduled survey for deletion.
   */
  ScheduledSurvey scheduleSurveyDeletion(SurveySchedulerDto schedulerDto);

  /**
   * Schedules the archiving of a survey.
   *
   * @param schedulerDto The SurveySchedulerDto object containing survey archiving scheduling
   *     information.
   * @return Scheduled survey for archiving.
   */
  ScheduledSurvey scheduleSurveyArchiving(SurveySchedulerDto schedulerDto);

  /**
   * Schedules the archiving of a survey.
   *
   * @param surveyId The surveyId string
   * @return Message to determine success or failure.
   */
  DistributionResponseDto deleteSurvey(String surveyId);

  /**
   * Schedules the archiving of a survey.
   *
   * @param managementDto The AccountManagementDto object containing
   * @return Survey.
   */
  com.amalitech.surveysphere.models.Survey activateOrDeactivateSurvey(
      AccountManagementDto managementDto);

  AllSurveysResponseDto getTemplate(int page, int limit, String tab);

  ExcelResponseDto exportExcel(String surveyId) throws IOException;

  String getUserId();

  DistributionResponseDto archiveSurvey(String surveyId);

  void markSurveyAsTakingResponsesOrNot(String surveyId, boolean isTakingResponses);
}
