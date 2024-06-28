package com.amalitech.surveysphere.controllers;

import com.amalitech.surveysphere.dto.requestDto.*;
import com.amalitech.surveysphere.dto.responseDto.*;
import com.amalitech.surveysphere.dto.responseDto.AllSurveysResponseDto;
import com.amalitech.surveysphere.dto.responseDto.DistributionResponseDto;
import com.amalitech.surveysphere.dto.responseDto.ErrorResponseDto;
import com.amalitech.surveysphere.models.ScheduledSurvey;
import com.amalitech.surveysphere.models.Survey;
import com.amalitech.surveysphere.services.responseManagementService.ResponseManagementService;
import com.amalitech.surveysphere.services.surveyService.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Controller class for handling user-related operations in the Survey Sphere application. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey-sphere/user")
@Tags({@Tag(name = "User"), @Tag(name = "Admin")})
@SecurityRequirement(name = "emailPasswordAuth")
@Validated
public class SurveyController {
  private final SurveyService surveyService;
  private final ResponseManagementService responseManagementService;
  private final HttpHeaders httpHeaders;

  /**
   * Creates a new survey.
   *
   * @param surveyDto The SurveyDto object containing survey information.
   * @return ResponseEntity indicating the success or failure of the survey creation process.
   */
  @PostMapping("/create-survey")
  @Operation(
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Survey created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "422",
            description = "Unprocessable Entity"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "409",
            description = "Survey already exists")
      },
      summary = "Create Survey",
      description = "Creates a new survey.")
  public ResponseEntity<Survey> createSurvey(@Valid @RequestBody SurveyDto surveyDto) {
    return new ResponseEntity<>(surveyService.createSurvey(surveyDto), HttpStatus.CREATED);
  }

  /**
   * Shares a survey with specified recipients.
   *
   * @param sharingDto The SharingDto object containing sharing information.
   * @return ResponseEntity indicating the success or failure of the survey sharing process.
   * @throws MessagingException if an error occurs during email sending.
   */
  @PostMapping("/share-survey")
  @Operation(
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey shared successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      },
      summary = "Share Survey",
      description = "Shares a survey with specified recipients.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Sharing object that needs to be created",
              content =
                  @io.swagger.v3.oas.annotations.media.Content(
                      mediaType = "application/json",
                      schema =
                          @io.swagger.v3.oas.annotations.media.Schema(
                              implementation = SharingDto.class))))
  public ResponseEntity<DistributionResponseDto> shareSurvey(@RequestBody SharingDto sharingDto)
      throws MessagingException {
    return new ResponseEntity<>(
        surveyService.distributeSurvey(
            sharingDto.getEmails(),
            sharingDto.getSubject(),
            sharingDto.getMessage(),
            sharingDto.getSurveyId()),
        HttpStatus.OK);
  }

  /**
   * Updates an existing survey.
   *
   * @param surveyDto The SurveyDto object containing updated survey information.
   * @param surveyId The ID of the survey to update.
   * @return ResponseEntity indicating the success or failure of the survey update process.
   */
  @PostMapping("/update-survey")
  @Operation(
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Survey object that needs to be updated",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = SurveyDto.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      },
      summary = "Update Survey",
      description = "Updates an existing survey.")
  public ResponseEntity<com.amalitech.surveysphere.models.Survey> updateSurvey(
      @RequestBody SurveyDto surveyDto,
      @RequestParam(value = "surveyId", required = false) String surveyId) {
    return new ResponseEntity<>(surveyService.updateSurvey(surveyDto, surveyId), HttpStatus.OK);
  }

  @GetMapping("/survey/sent")
  @Operation(
      summary = "Get Survey",
      description = "Retrieves a survey by ID.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  public ResponseEntity<AllSurveysResponseDto> getSentSurveys(
      @RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) {
    return new ResponseEntity<>(surveyService.getSentSurveys(page, limit), HttpStatus.OK);
  }

  @GetMapping("/survey/draft")
  @Operation(
      summary = "Get Survey",
      description = "Retrieves a survey by ID.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  public ResponseEntity<AllSurveysResponseDto> getDraftSurveys(
      @RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) {
    return new ResponseEntity<>(surveyService.getDraftSurveys(page, limit), HttpStatus.OK);
  }

  @GetMapping("/survey/archived")
  @Operation(
      summary = "Get Survey",
      description = "Retrieves a survey by ID.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  public ResponseEntity<AllSurveysResponseDto> getArchivedSurveys(
      @RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) {
    return new ResponseEntity<>(surveyService.getArchivedSurveys(page, limit), HttpStatus.OK);
  }

  @GetMapping("/survey/scheduled")
  @Operation(
      summary = "Get Survey",
      description = "Retrieves a survey by ID.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  public ResponseEntity<AllSurveysResponseDto> getScheduledSurveys(
      @RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) {
    return new ResponseEntity<>(surveyService.getScheduledSurveys(page, limit), HttpStatus.OK);
  }

  /**
   * Retrieves a survey by ID.
   *
   * @param surveyId The ID of the survey to retrieve.
   * @return Object representing the retrieved survey.
   */
  @GetMapping("/get-survey/{surveyId}")
  @Operation(
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey settings customized successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      },
      summary = "Customize Survey",
      description = "Customizes survey settings.")
  public ResponseEntity<com.amalitech.surveysphere.models.Survey> getSurvey(
      @PathVariable("surveyId") String surveyId) {
    return new ResponseEntity<>(surveyService.getSurvey(surveyId), HttpStatus.OK);
  }

  /**
   * Schedules survey distribution.
   *
   * @param surveySchedulerDto The SurveySchedulerDto object containing survey distribution
   *     scheduling information.
   * @return A ResponseEntity containing the scheduled survey object with HTTP status code 200 (OK).
   */
  @PostMapping("/schedule-survey-distribution")
  @Operation(
      summary = "Schedule Survey Distribution",
      description = "Schedules survey distribution.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey distribution scheduled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "409",
            description = "Survey already scheduled for distribution"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  public ResponseEntity<ScheduledSurvey> scheduleSurveyDistribution(
      @RequestBody SurveySchedulerDto surveySchedulerDto) {
    return new ResponseEntity<>(
        surveyService.scheduleSurveyDistribution(surveySchedulerDto), HttpStatus.OK);
  }

  /**
   * Schedules survey deletion.
   *
   * @param surveySchedulerDto The SurveySchedulerDto object containing survey distribution
   *     scheduling information.
   * @return A ResponseEntity containing the scheduled survey object with HTTP status code 200 (OK).
   */
  @PostMapping("/schedule-survey-deletion")
  @Operation(
      summary = "Schedule Survey Deletion",
      description = "Schedules survey deletion.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey deletion scheduled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "409",
            description = "Survey already scheduled for deletion"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  public ResponseEntity<ScheduledSurvey> scheduleSurveyDelete(
      @RequestBody SurveySchedulerDto surveySchedulerDto) {
    return new ResponseEntity<>(
        surveyService.scheduleSurveyDeletion(surveySchedulerDto), HttpStatus.OK);
  }

  /**
   * Schedules survey archiving.
   *
   * @param surveySchedulerDto The SurveySchedulerDto object containing survey archiving scheduling
   *     information.
   * @return ResponseEntity indicating the success or failure of the survey archiving scheduling
   *     process.
   */
  @PostMapping("/schedule-survey-archiving")
  @Operation(
      summary = "Schedule Survey Archiving",
      description = "Schedules survey archiving.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey archiving scheduled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "409",
            description = "Survey already scheduled for archiving"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  public ResponseEntity<ScheduledSurvey> scheduleSurveyArchiving(
      @RequestBody SurveySchedulerDto surveySchedulerDto) {
    return new ResponseEntity<>(
        surveyService.scheduleSurveyArchiving(surveySchedulerDto), HttpStatus.OK);
  }

  @GetMapping("/response/{surveyId}")
  @Operation(
      summary = "Get survey responses",
      description = "Retrieves survey respondents along with their responses to questions",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Response analysis dto"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  public ResponseEntity<ResponseAnalysisDto> getResponses(
      @PathVariable("surveyId") String surveyId) {
    return new ResponseEntity<>(responseManagementService.getResponses(surveyId), HttpStatus.OK);
  }

  @GetMapping("/generate-excels")
  public ResponseEntity<ExcelResponseDto> exportExcel(@RequestParam("surveyId") String surveyId)
      throws IOException {
    httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    httpHeaders.setContentDispositionFormData("attachment", "filename" + ".xlsx");

    return ResponseEntity.ok().body(surveyService.exportExcel(surveyId));
  }

  @Operation(
      summary = "Archive a survey",
      description = "Archives a selected survey by the user",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Distribution Response dto"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)),
            responseCode = "404",
            description = "Survey not found")
      })
  @PatchMapping("/survey/archive")
  public ResponseEntity<DistributionResponseDto> archiveSurvey(@RequestBody ArchiveDto archiveDto) {
    return new ResponseEntity<>(
        surveyService.archiveSurvey(archiveDto.getSurveyId()), HttpStatus.OK);
  }

  @DeleteMapping("/survey/{surveyId}")
  @Operation(
      summary = "Delete a survey",
      description = "Deletes a specific survey.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Survey deleted successfully"),
      })
  public ResponseEntity<DistributionResponseDto> deleteSurvey(
      @PathVariable("surveyId") String surveyId) {
    return new ResponseEntity<>(surveyService.deleteSurvey(surveyId), HttpStatus.OK);
  }

  @GetMapping("/response")
  @Operation(
      summary = "Inactivate a survey",
      description = "Marks a specific survey as inactive.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200"),
      })
  public ResponseEntity<Void> markSurveyAsTakingResponsesOrNot(
      @RequestParam(name = "surveyId") String surveyId,
      @RequestParam(name = "isTakingResponses") boolean isTakingResponses) {
    surveyService.markSurveyAsTakingResponsesOrNot(surveyId, isTakingResponses);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
