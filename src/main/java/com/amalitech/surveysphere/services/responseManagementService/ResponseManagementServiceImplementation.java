package com.amalitech.surveysphere.services.responseManagementService;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.*;

import com.amalitech.surveysphere.dto.requestDto.ResponseManagementDto;
import com.amalitech.surveysphere.dto.responseDto.ResponseAnalysisDto;
import com.amalitech.surveysphere.dto.responseDto.ResponseManagementResponseDto;
import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.exceptions.UnauthorizedException;
import com.amalitech.surveysphere.models.*;
import com.amalitech.surveysphere.repositories.*;
import com.amalitech.surveysphere.services.otherServices.timeservice.TimeService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/** Service implementation for managing responses to surveys. */
@Service
@RequiredArgsConstructor
public class ResponseManagementServiceImplementation implements ResponseManagementService {
  private final RespondentRepository respondentRepository;
  private final UserRepository userRepository;
  private final SurveyRepository surveyRepository;
  private final QuestionRepository questionRepository;
  private final ResponseRepository responseRepository;
  private final TimeService timeService;

  /**
   * Records responses to a survey.
   *
   * @param managementDto The ResponseManagementDto containing response details.
   * @param respondentId The ID of the respondent submitting the responses.
   * @return A ResponseManagementResponseDto representing the result of the response recording
   *     process.
   */
  @Override
  public ResponseManagementResponseDto recordResponses(
      ResponseManagementDto managementDto, String respondentId) {

    String message = "Response recorded successfully";

    Optional<Survey> surveyExists = surveyRepository.findById(managementDto.getSurveyId());

    if (surveyExists.isEmpty()) throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());

    Survey survey = surveyExists.get();

    if (!(survey.isSent())) survey.setSent(true);

    int responseCount = respondentRepository.countResponsesBySurveyId(survey.getId()) + 1;

    if (responseCount == 0) {
      responseCount = 1;
    }

    survey.setRespondentCount(responseCount);
    surveyRepository.save(survey);

    if (!survey.isActive())
      throw new UnauthorizedException("This survey is no longer taking responses");

    if (respondentId != null) {
      Optional<Respondent> respondentExists = respondentRepository.findById(respondentId);

      if (respondentExists.isPresent())
        return ResponseManagementResponseDto.builder()
            .time(respondentExists.get().getCreatedDate().toString())
            .respondentId(updateRespondent(respondentExists.get(), managementDto).getId())
            .message(message)
            .build();
    }

    List<Response> responses = responseRepository.saveAll(managementDto.getResponses());
    updateAnsweredAndSkipped(responses);

    Respondent respondent = returnCreatedRespondent(managementDto, responses);
    return ResponseManagementResponseDto.builder()
        .time(respondent.getCreatedDate().toString())
        .respondentId(respondent.getId())
        .message(message)
        .build();
  }

  @Override
  public ResponseAnalysisDto getResponses(String surveyId) {
    Optional<Survey> surveyExist = surveyRepository.findById(surveyId);

    if (surveyExist.isEmpty()) throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());

    Survey survey = surveyExist.get();
    List<Question> questions =
        survey.getBlocks().stream().flatMap(block -> block.getQuestions().stream()).toList();

    List<AllResponses> allResponses =
        questions.stream()
            .map(
                question ->
                    AllResponses.builder()
                        .question(question.getTitle().getQuestion())
                        .questionType(question.getType())
                        .answered(question.getAnswered())
                        .skipped(question.getSkipped())
                        .responses(responseRepository.findAllByQuestionId(question.getId()))
                        .options(question.getOptions())
                        .build())
            .toList();

    List<Respondent> respondents = respondentRepository.findBySurveyId(surveyId);

    return ResponseAnalysisDto.builder()
        .averageTime(timeService.calculateAverageTimeBySurveyId(surveyId))
        .responseCount(respondents.size())
        .active(survey.isActive())
        .responses(allResponses)
        .individualResults(respondents)
        .build();
  }

  private void updateAnsweredAndSkipped(List<Response> responses) {
    for (Response response : responses) {
      Optional<Question> questionExist = questionRepository.findById(response.getQuestionId());

      if (questionExist.isEmpty()) throw new NotFoundException(QUESTION_NOT_FOUND.getMessage());

      Question question = questionExist.get();

      if (response.getAnswer().isEmpty()
          || response.getAnswer().stream().anyMatch(String::isBlank)) {
        int skipped = question.getSkipped();
        question.setSkipped(skipped + 1);
        questionRepository.save(question);
      }

      if (!response.getAnswer().isEmpty()
          && response.getAnswer().stream().noneMatch(String::isBlank)) {
        int answered = question.getAnswered();
        question.setAnswered(answered + 1);
        questionRepository.save(question);
      }
    }
  }

  private Respondent updateRespondent(Respondent respondent, ResponseManagementDto managementDto) {
    respondent.getResponses().addAll(managementDto.getResponses());
    return respondentRepository.save(respondent);
  }

  /**
   * Returns a newly created Respondent based on the provided details in the request.
   *
   * @param request The ResponseManagementDto containing request details.
   * @param responses The list of responses associated with the respondent.
   * @return The newly created Respondent.
   */
  private Respondent returnCreatedRespondent(
      ResponseManagementDto request, List<Response> responses) {
    Optional<String> platformUser = isPlatformUser(request);

    return platformUser
        .map(s -> createRespondent(s, request.getSurveyId(), false, responses))
        .orElseGet(() -> createRespondent(responses, request.getSurveyId()));
  }

  /**
   * Creates a new Respondent with anonymous status and the provided list of responses.
   *
   * @param responses The list of responses associated with the respondent.
   * @return The newly created Respondent.
   */
  private Respondent createRespondent(List<Response> responses, String surveyId) {
    return createRespondent(null, surveyId, true, responses);
  }

  /**
   * Creates a new Respondent with the provided email, anonymous status, and list of responses.
   *
   * @param email The email of the respondent.
   * @param isAnonymous The anonymous status of the respondent.
   * @param responses The list of responses associated with the respondent.
   * @return The newly created Respondent.
   */
  private Respondent createRespondent(
      String email, String surveyId, boolean isAnonymous, List<Response> responses) {
    Respondent respondent =
        Respondent.builder()
            .email(email)
            .isAnonymous(isAnonymous)
            .responses(responses)
            .surveyId(surveyId)
            .build();

    return respondentRepository.save(respondent);
  }

  /**
   * Determines if the user submitting the response is a platform user based on the provided email.
   * If the email is not provided, it checks if the authenticated user is a platform user.
   *
   * @param managementDto The ResponseManagementDto containing request details.
   * @return An Optional containing the email of the platform user if found, or empty if not a
   *     platform user.
   */
  private Optional<String> isPlatformUser(ResponseManagementDto managementDto) {
    String email = managementDto.getEmail();

    if (email != null) {
      Optional<User> userExists = userRepository.findByEmail(email);

      if (userExists.isEmpty()
          && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
        String isAuthenticatedUser = getAuthenticatedUser();

        if (isAuthenticatedUser != null) return isAuthenticatedUser.describeConstable();
      }
      return userExists
          .map(user -> user.getEmail().describeConstable())
          .orElseGet(email::describeConstable);

    } else {
      String isAuthenticatedUser = getAuthenticatedUser();

      if (isAuthenticatedUser != null) return isAuthenticatedUser.describeConstable();
    }

    return Optional.empty();
  }

  /**
   * Retrieves the username of the authenticated user, if available.
   *
   * @return The username of the authenticated user, or null if authentication information is not
   *     available.
   */
  private String getAuthenticatedUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!(principal instanceof UserDetails)) return null;

    org.springframework.security.core.userdetails.User userDetails =
        (org.springframework.security.core.userdetails.User) principal;
    return userDetails.getUsername();
  }
}
