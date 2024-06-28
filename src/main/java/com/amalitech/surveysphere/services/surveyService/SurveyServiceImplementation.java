package com.amalitech.surveysphere.services.surveyService;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.*;
import static com.amalitech.surveysphere.enums.Role.*;

import com.amalitech.surveysphere.dto.requestDto.*;
import com.amalitech.surveysphere.dto.responseDto.AllSurveysResponseDto;
import com.amalitech.surveysphere.dto.responseDto.DistributionResponseDto;
import com.amalitech.surveysphere.dto.responseDto.ExcelResponseDto;
import com.amalitech.surveysphere.exceptions.DuplicateException;
import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.models.*;
import com.amalitech.surveysphere.repositories.*;
import com.amalitech.surveysphere.services.otherServices.excelService.ExcelService;
import com.amalitech.surveysphere.services.otherServices.mailService.MailService;
import com.amalitech.surveysphere.services.otherServices.uploadService.UploadService;
import com.amalitech.surveysphere.services.questionService.QuestionService;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service class implementing SurveyService interface. Provides operations related to survey
 * management.
 */
@Service
@RequiredArgsConstructor
public class SurveyServiceImplementation implements SurveyService {
  private final UserRepository userRepository;
  private final SurveyRepository surveyRepository;
  private final MailService mailService;
  private final UploadService uploadService;
  private final ExcelService excelService;
  private final QuestionService questionService;
  private final Environment env;
  private final ScheduledSurveyRepository scheduledSurveyRepository;

  /**
   * Creates a new survey based on the provided SurveyDto.
   *
   * @param surveyDto The SurveyDto containing survey details.
   * @return Survey.
   */
  @Override
  public Survey createSurvey(SurveyDto surveyDto) {

    String userId = getUserId();
    Optional<User> user = userRepository.findById(userId);

    if (user.isEmpty()) throw new NotFoundException(USER_NOT_FOUND.getMessage());

    String role = user.get().getRole();

    FileUploadDto fileUploadDto =
        FileUploadDto.builder()
            .style(surveyDto.getLogo().getStyle())
            .url(uploadService.uploadFile(surveyDto.getLogo()))
            .build();

    com.amalitech.surveysphere.models.Survey survey =
        com.amalitech.surveysphere.models.Survey.builder()
            .logo(fileUploadDto)
            .surveyTitle(surveyDto.getSurveyTitle())
            .surveyOwner(getUserId())
            .blocks(questionService.saveBlocks(surveyDto.getBlocks()))
            .colorScheme(surveyDto.getColorScheme())
            .template(role.equals(ADMIN.name()))
            .category(surveyDto.getCategory())
            .surveyView(surveyDto.getSurveyView())
            .premium(surveyDto.isPremium())
            .build();

    com.amalitech.surveysphere.models.Survey savedSurvey = surveyRepository.save(survey);
    savedSurvey.setSurveyLink(
        Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN"))
            .concat("/survey/")
            .concat(savedSurvey.getId()));
    surveyRepository.save(savedSurvey);

    User findUser =
        userRepository
            .findById(getUserId())
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

    findUser.getSurveys().add(savedSurvey);

    userRepository.save(findUser);
    return savedSurvey;
  }

  /**
   * Updates a survey with the provided details.
   *
   * @param surveyDto The SurveyDto containing the updated survey details.
   * @param surveyId The ID of the survey to be updated.
   * @return The updated survey.
   */
  @Override
  public com.amalitech.surveysphere.models.Survey updateSurvey(
      SurveyDto surveyDto, String surveyId) {

    Optional<com.amalitech.surveysphere.models.Survey> surveyExists =
        surveyRepository.findById(surveyId);

    if (surveyExists.isEmpty()) throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());

    FileUploadDto fileUploadDto =
        FileUploadDto.builder()
            .style(surveyDto.getLogo().getStyle())
            .url(uploadService.uploadFile(surveyDto.getLogo()))
            .build();

    com.amalitech.surveysphere.models.Survey survey = surveyExists.get();
    survey.setSurveyTitle(surveyDto.getSurveyTitle());
    survey.setCategory(surveyDto.getCategory());
    survey.setSurveyView(surveyDto.getSurveyView());
    survey.setColorScheme(surveyDto.getColorScheme());
    survey.setLogo(fileUploadDto);
    survey.setBlocks(questionService.updateBlocks(surveyDto.getBlocks()));

    return surveyRepository.save(survey);
  }

  /**
   * Retrieves all active surveys.
   *
   * @return All active surveys.
   */
  @Override
  public AllSurveysResponseDto getActiveSurveys(int page, int limit, String uuid) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<com.amalitech.surveysphere.models.Survey> surveys =
        surveyRepository.findAllBySurveyOwnerAndDeactivated(uuid, false, pageable);

    return AllSurveysResponseDto.builder()
        .surveys(surveys.getContent())
        .totalPages(surveys.getTotalPages())
        .build();
  }

  /**
   * Retrieves active surveys by survey owner.
   *
   * @return All survey responses.
   */
  @Override
  public AllSurveysResponseDto getSentSurveys(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<com.amalitech.surveysphere.models.Survey> surveys =
        surveyRepository.findAllBySentAndSurveyOwnerAndDeleted(true, getUserId(), false, pageable);

    return AllSurveysResponseDto.builder()
        .surveys(surveys.getContent())
        .totalPages(surveys.getTotalPages())
        .build();
  }

  /**
   * Retrieves drafted surveys by survey owner.
   *
   * @return All survey responses.
   */
  @Override
  public AllSurveysResponseDto getDraftSurveys(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    String userId = getUserId();
    List<Survey> content = new ArrayList<>();

    Optional<User> userExists = userRepository.findById(userId);

    Page<com.amalitech.surveysphere.models.Survey> surveys =
        surveyRepository.findAllBySurveyOwnerAndSentAndArchivedAndDeleted(
            userId, false, false, false, pageable);

    if (userExists.isEmpty()) {
      content = surveys.getContent();
    } else {
      List<Survey> userSurveys =
          userExists.get().getSurveys().stream()
              .filter(survey -> !Objects.equals(survey.getSurveyOwner(), userId))
              .toList();
      content.addAll(userSurveys);
      content.addAll(surveys.getContent());
    }

    return AllSurveysResponseDto.builder()
        .surveys(content)
        .totalPages(surveys.getTotalPages())
        .build();
  }

  /**
   * Retrieves archived surveys by survey owner.
   *
   * @return All survey responses.
   */
  @Override
  public AllSurveysResponseDto getArchivedSurveys(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<com.amalitech.surveysphere.models.Survey> surveys =
        surveyRepository.findAllByArchivedAndSurveyOwnerAndDeleted(
            true, getUserId(), false, pageable);

    return AllSurveysResponseDto.builder()
        .surveys(surveys.getContent())
        .totalPages(surveys.getTotalPages())
        .build();
  }

  /**
   * Retrieves scheduled surveys by survey owner.
   *
   * @return All survey responses.
   */
  @Override
  public AllSurveysResponseDto getScheduledSurveys(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<com.amalitech.surveysphere.models.Survey> surveys =
        surveyRepository.findAllBySurveyOwnerAndDeactivated(getUserId(), false, pageable);
    List<com.amalitech.surveysphere.models.Survey> scheduledSurveys =
        surveys.getContent().stream()
            .filter(
                f -> {
                  ScheduledSurvey scheduledSurvey = f.getScheduledSurvey();
                  return scheduledSurvey != null && !scheduledSurvey.isCompleted();
                })
            .toList();

    return AllSurveysResponseDto.builder()
        .surveys(scheduledSurveys)
        .totalPages(surveys.getTotalPages())
        .build();
  }

  public String getUserId() {
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> findUser = userRepository.findByEmail(userDetails.getUsername());

    if (findUser.isEmpty()) throw new NotFoundException(USER_NOT_AUTHENTICATED.getMessage());

    return findUser.get().getId();
  }

  /**
   * Archives a survey by its ID.
   *
   * @param surveyId The ID of the survey to retrieve.
   * @return A Distribution response dto.
   */
  @Override
  public DistributionResponseDto archiveSurvey(String surveyId) {
    Survey survey =
        surveyRepository
            .findById(surveyId)
            .orElseThrow(() -> new NotFoundException(SURVEY_NOT_FOUND.getMessage()));

    survey.setArchived(true);
    surveyRepository.save(survey);

    return new DistributionResponseDto("Survey archived");
  }

  @Override
  public void markSurveyAsTakingResponsesOrNot(String surveyId, boolean isTakingResponses) {
    Survey survey =
        surveyRepository
            .findById(surveyId)
            .orElseThrow(() -> new NotFoundException(SURVEY_NOT_FOUND.getMessage()));

    survey.setActive(isTakingResponses);
    surveyRepository.save(survey);
  }

  /**
   * Retrieves a survey by its ID.
   *
   * @param surveyId The ID of the survey to retrieve.
   * @return A survey.
   */
  @Override
  //  @Cacheable(value = "survey", key = "#surveyId")
  public com.amalitech.surveysphere.models.Survey getSurvey(String surveyId) {
    Optional<com.amalitech.surveysphere.models.Survey> survey = surveyRepository.findById(surveyId);

    if (survey.isEmpty()) throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());

    if (survey.get().isDeleted()) throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());

    return survey.get();
  }

  /**
   * Distributes a survey to specified email addresses.
   *
   * @param emails The list of email addresses to send the survey to.
   * @param subject The subject of the email containing the survey.
   * @param message The message body of the email containing the survey.
   * @param surveyId The ID of the survey to distribute.
   * @return A Distribution Response Dto.
   */
  @Override
  public DistributionResponseDto distributeSurvey(
      List<String> emails, String subject, String message, String surveyId)
      throws MessagingException {
    com.amalitech.surveysphere.models.Survey survey =
        surveyRepository.findById(surveyId).orElse(null);
    if (survey != null) {
      String surveyUrl = survey.getSurveyLink();
      if (!survey.isSent()) {
        survey.setSent(true);
        surveyRepository.save(survey);
      }

      for (String email : emails) {
        mailService.sendSurvey(email, subject, message, surveyUrl);
      }

      return DistributionResponseDto.builder().message("Email Sent Successfully").build();
    }

    return DistributionResponseDto.builder().message("Failed to send email").build();
  }

  /**
   * Schedules the distribution of a survey.
   *
   * @param schedulerDto The SurveySchedulerDto containing the details of the scheduled
   *     distribution.
   * @return A Scheduled Survey for distribution.
   */
  @Override
  public ScheduledSurvey scheduleSurveyDistribution(SurveySchedulerDto schedulerDto) {

    checkForExistingSurvey(schedulerDto.getSharingInfo().getSurveyId());

    checkForExistingSchedule(schedulerDto.getSharingInfo().getSurveyId(), "distribute");

    LocalDateTime scheduledDate =
        convertSchecduledDateToLocalDateTime(schedulerDto.getScheduleDate());

    ScheduledSurvey scheduledSurvey =
        ScheduledSurvey.builder()
            .scheduledDate(scheduledDate)
            .surveyId(schedulerDto.getSharingInfo().getSurveyId())
            .message(schedulerDto.getSharingInfo().getMessage())
            .subject(schedulerDto.getSharingInfo().getSubject())
            .emails(schedulerDto.getSharingInfo().getEmails())
            .action("distribute")
            .build();

    com.amalitech.surveysphere.models.Survey survey = getSurvey(scheduledSurvey.getSurveyId());
    survey.setScheduledSurvey(scheduledSurvey);
    surveyRepository.save(survey);

    return scheduledSurveyRepository.save(scheduledSurvey);
  }

  /**
   * Schedules the deletion of a survey.
   *
   * @param schedulerDto The SurveySchedulerDto containing the details of the scheduled deletion.
   * @return A ScheduledSurvey representing the scheduled deletion.
   */
  @Override
  public ScheduledSurvey scheduleSurveyDeletion(SurveySchedulerDto schedulerDto) {

    checkForExistingSurvey(schedulerDto.getSharingInfo().getSurveyId());

    checkForExistingSchedule(schedulerDto.getSharingInfo().getSurveyId(), "delete");

    LocalDateTime scheduledDate =
        convertSchecduledDateToLocalDateTime(schedulerDto.getScheduleDate());

    ScheduledSurvey scheduledSurvey =
        ScheduledSurvey.builder()
            .scheduledDate(scheduledDate)
            .surveyId(schedulerDto.getSharingInfo().getSurveyId())
            .action("delete")
            .build();

    com.amalitech.surveysphere.models.Survey survey = getSurvey(scheduledSurvey.getSurveyId());
    survey.setScheduledSurvey(scheduledSurvey);
    surveyRepository.save(survey);

    return (scheduledSurveyRepository.save(scheduledSurvey));
  }

  /**
   * Schedules the archiving of a survey.
   *
   * @param schedulerDto The SurveySchedulerDto containing the details of the scheduled archiving.
   * @return A ScheduledSurvey representing the scheduled archiving.
   */
  @Override
  public ScheduledSurvey scheduleSurveyArchiving(SurveySchedulerDto schedulerDto) {

    checkForExistingSurvey(schedulerDto.getSharingInfo().getSurveyId());

    checkForExistingSchedule(schedulerDto.getSharingInfo().getSurveyId(), "archive");

    LocalDateTime scheduledDate =
        convertSchecduledDateToLocalDateTime(schedulerDto.getScheduleDate());

    ScheduledSurvey scheduledSurvey =
        ScheduledSurvey.builder()
            .scheduledDate(scheduledDate)
            .surveyId(schedulerDto.getSharingInfo().getSurveyId())
            .action("archive")
            .build();

    com.amalitech.surveysphere.models.Survey survey = getSurvey(scheduledSurvey.getSurveyId());
    survey.setScheduledSurvey(scheduledSurvey);
    surveyRepository.save(survey);

    return scheduledSurveyRepository.save(scheduledSurvey);
  }

  @Override
  public DistributionResponseDto deleteSurvey(String surveyId) {
    Optional<com.amalitech.surveysphere.models.Survey> surveyExist =
        surveyRepository.findById(surveyId);

    if (surveyExist.isEmpty()) throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());

    com.amalitech.surveysphere.models.Survey survey = surveyExist.get();
    survey.setDeleted(true);
    surveyRepository.save(survey);

    return DistributionResponseDto.builder().message("Survey deleted successfully").build();
  }

  /**
   * Checks for an existing schedule for the given survey ID and action.
   *
   * @param surveyId The ID of the survey.
   * @param action The action for which the schedule is checked.
   */
  private void checkForExistingSchedule(String surveyId, String action) {
    Optional<ScheduledSurvey> scheduledSurveyExist =
        scheduledSurveyRepository.findBySurveyId(surveyId);

    if (scheduledSurveyExist.isPresent() && scheduledSurveyExist.get().getAction().equals(action))
      throw new DuplicateException("Survey already scheduled for " + action);
  }

  /**
   * Checks if there is an existing survey for the given survey ID.
   *
   * @param surveyId The ID of the survey
   * @throws NotFoundException If the survey with the provided ID is not found
   */
  private void checkForExistingSurvey(String surveyId) {
    Optional<com.amalitech.surveysphere.models.Survey> survey = surveyRepository.findById(surveyId);

    if (survey.isEmpty()) throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());
  }

  /**
   * Converts the scheduled date string to LocalDateTime.
   *
   * @param scheduleDate The scheduled date string in a specific format.
   * @return The LocalDateTime representation of the scheduled date.
   */
  private LocalDateTime convertSchecduledDateToLocalDateTime(String scheduleDate) {
    return LocalDateTime.parse(
        scheduleDate + ":00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  /**
   * Converts the scheduled date string to LocalDateTime.
   *
   * @param managementDto The scheduled date string in a specific format.
   * @return A Survey.
   * @throws NotFoundException when an id is not found
   */
  @Override
  public com.amalitech.surveysphere.models.Survey activateOrDeactivateSurvey(
      AccountManagementDto managementDto) {

    com.amalitech.surveysphere.models.Survey surveyExists =
        surveyRepository
            .findById(managementDto.getId())
            .orElseThrow(() -> new NotFoundException(SURVEY_NOT_FOUND.getMessage()));

    surveyExists.setDeactivated(managementDto.getStatus().equals("DEACTIVATE"));
    return surveyRepository.save(surveyExists);
  }

  /**
   * Retrieves surveys based on pagination and tab filter.
   *
   * @param page The page number for pagination
   * @param limit The maximum number of surveys per page
   * @param tab The tab filter (e.g., "all" or "deactivated")
   * @return AllSurveysResponseDto containing the surveys and total pages
   * @throws IllegalArgumentException If an unexpected tab value is provided
   */
  @Override
  public AllSurveysResponseDto getTemplate(int page, int limit, String tab) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<com.amalitech.surveysphere.models.Survey> surveys;
    if (tab == null) {
      surveys =
          surveyRepository.findAllByTemplateAndDeletedAndDeactivated(true, false, false, pageable);
    } else {
      surveys =
          switch (tab.toLowerCase()) {
            case "all" ->
                surveyRepository.findAllByTemplateAndDeletedAndDeactivated(
                    true, false, false, pageable);
            case "deactivated" ->
                surveyRepository.findAllByDeactivatedAndTemplateAndDeleted(
                    true, true, false, pageable);
            default -> throw new IllegalArgumentException("Unexpected value: " + tab.toLowerCase());
          };
    }
    return AllSurveysResponseDto.builder()
        .surveys(surveys.getContent())
        .totalPages(surveys.getTotalPages())
        .build();
  }

  /**
   * Exports survey data to Excel format.
   *
   * @param surveyId The ID of the survey to be exported
   * @return ExcelResponseDto containing the Excel file bytes and filename
   * @throws IOException If an I/O error occurs while generating the Excel file
   * @throws NotFoundException If the survey with the provided ID is not found
   */
  @Override
  public ExcelResponseDto exportExcel(String surveyId) throws IOException {
    Optional<com.amalitech.surveysphere.models.Survey> surveyExist =
        surveyRepository.findById(surveyId);

    if (surveyExist.isEmpty()) throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());

    String fileName = surveyExist.get().getSurveyTitle().getTitle().getTitleName();

    byte[] bytes = excelService.generateExcelFiles(surveyId);
    return new ExcelResponseDto(bytes, fileName);
  }
}
