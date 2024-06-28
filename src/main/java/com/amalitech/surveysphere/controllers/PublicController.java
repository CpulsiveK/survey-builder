package com.amalitech.surveysphere.controllers;

import static com.amalitech.surveysphere.services.invitationService.InvitationServiceImplementation.generateSHA256;

import com.amalitech.surveysphere.dto.requestDto.*;
import com.amalitech.surveysphere.dto.responseDto.*;
import com.amalitech.surveysphere.exceptions.DuplicateException;
import com.amalitech.surveysphere.models.Collaborators;
import com.amalitech.surveysphere.models.Survey;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.UserRepository;
import com.amalitech.surveysphere.services.aiservice.AiService;
import com.amalitech.surveysphere.services.invitationService.InvitationService;
import com.amalitech.surveysphere.services.responseManagementService.ResponseManagementService;
import com.amalitech.surveysphere.services.socialUserService.SocialUserService;
import com.amalitech.surveysphere.services.surveyService.SurveyService;
import com.amalitech.surveysphere.services.userService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Mono;

/** Controller class for handling public-facing operations in the Survey Sphere application. */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey-sphere/public")
@Tags({@Tag(name = "User"), @Tag(name = "Admin")})
public class PublicController {
  private final SocialUserService socialUserService;
  private final UserService userService;
  private final SurveyService surveyService;
  private final UserRepository userRepository;
  private final ResponseManagementService responseManagementService;
  private final Environment env;
  private final InvitationService invitationService;

  /**
   * Handles social login requests.
   *
   * @param jwtDto The JwtDto object containing JWT token information.
   * @param authentication The authentication object.
   * @param httpServletRequest The HttpServletRequest object.
   * @return ResponseEntity indicating the success or failure of the social login process.
   * @throws GeneralSecurityException if an error occurs during security operations.
   * @throws ParseException if an error occurs during parsing.
   * @throws IOException if an I/O error occurs.
   */
  @PostMapping("/social-login")
  @Operation(
      summary = "Social Login",
      description = "Handles social login requests.",
      responses = {@ApiResponse(responseCode = "200", description = "Social login successful")})
  public ResponseEntity<UserResponseDto> socialLogin(
      @Valid @RequestBody JwtDto jwtDto,
      Authentication authentication,
      HttpServletRequest httpServletRequest)
      throws GeneralSecurityException, ParseException, IOException {
    return new ResponseEntity<>(
        socialUserService.socialLogin(authentication, jwtDto, httpServletRequest), HttpStatus.OK);
  }

  /**
   * Registers a new user.
   *
   * @param userDto The UserDto object containing user information for registration.
   * @return ResponseEntity indicating the success or failure of the registration process.
   */
  @PostMapping("/signup")
  @Operation(
      summary = "Register User",
      description = "Registers a new user.",
      responses = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "409", description = "User already exists"),
        @ApiResponse(responseCode = "422", description = "Unprocessable entity"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserDto userDto)
      throws MessagingException {
    return ResponseEntity.ok(userService.registerUser(userDto));
  }

  /**
   * Verifies a user's account.
   *
   * @param userId The user ID.
   * @param token The verification token.
   * @return RedirectView indicating the result of the account verification.
   */
  @GetMapping("/verify-account/{userId}")
  @Operation(
      summary = "Verify Account",
      description = "Verifies a user's account.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Account verified successfully"),
        @ApiResponse(responseCode = "302", description = "Redirect to  verification page failed")
      })
  public RedirectView verifyAccount(
      @PathVariable("userId") String userId, @RequestParam("token") String token) {

    if (!userService.verifyAccount(userId, token))
      return new RedirectView(
          Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN"))
              .concat("/failed_Verification"));

    return new RedirectView(
        Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN")).concat("/verify_email"));
  }

  /**
   * Initiates a request to reset the user's password.
   *
   * @param requestDto The PasswordResetRequestDto object containing password reset request
   *     information.
   * @return ResponseEntity indicating the success or failure of the password reset request.
   */
  @PostMapping("/request-password")
  @Operation(
      summary = "Request Password",
      description = "Initiates a request to reset the user's password.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Password reset request initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<?> requestPassword(@Valid @RequestBody PasswordResetRequestDto requestDto)
      throws MessagingException {
    return ResponseEntity.ok(userService.requestPasswordReset(requestDto));
  }

  /**
   * Verifies a password reset request.
   *
   * @param userId The user ID.
   * @param code The verification code.
   * @return ResponseEntity indicating the success or failure of the password reset request
   *     verification.
   */
  @PostMapping("/verify-request/{userId}")
  @Operation(
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Password reset request verified successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      },
      summary = "Verify Password Reset Request",
      description = "Verifies a password reset request.")
  public ResponseEntity<PasswordRequestVerificationResponseDto> verifyPasswordResetRequest(
      @PathVariable("userId") String userId, @RequestParam("code") String code) {
    return ResponseEntity.ok(userService.verifyRequest(code, userId));
  }

  /**
   * Resets the user's password.
   *
   * @param userId The user ID.
   * @param resetPasswordDto The ResetPasswordDto object containing new password information.
   * @return ResponseEntity indicating the success or failure of the password reset operation.
   */
  @PatchMapping("/reset-password/{userId}")
  @Operation(summary = "Reset Password", description = "Resets the user's password.")
  public ResponseEntity<?> resetPassword(
      @PathVariable("userId") String userId,
      @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
    return ResponseEntity.ok(userService.resetPassword(resetPasswordDto, userId));
  }

  /**
   * Retrieves a survey by ID.
   *
   * @param surveyId The ID of the survey.
   * @return Object representing the retrieved survey.
   */
  @GetMapping("/survey/{surveyId}")
  @Operation(
      summary = "Get Survey",
      description = "Retrieves a survey by ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Survey retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Survey not found")
      })
  public ResponseEntity<Survey> getSurvey(@PathVariable("surveyId") String surveyId) {
    return new ResponseEntity<>(surveyService.getSurvey(surveyId), HttpStatus.OK);
  }

//  /**
//   * Retrieves all users.
//   *
//   * @return List of users.
//   */
//  @GetMapping("/get-users")
//  @Operation(
//      summary = "Get Users",
//      description = "Retrieves a list of users.",
//      responses = {
//        @ApiResponse(responseCode = "200", description = "All users retrieved successfully"),
//      })
//  public List<User> getUsers() {
//    return userRepository.findAll();
//  }

  /**
   * Handles POST requests to record responses from a respondent.
   *
   * @param request The request body containing the responses to be recorded.
   * @param respondentId The ID of the respondent whose responses are being recorded.
   * @return ResponseEntity containing ResponseManagementResponseDto with the recorded responses and
   *     HTTP status code indicating success.
   */
  @PostMapping("/response")
  @Operation(
      responses = {
        @ApiResponse(responseCode = "200", description = "Responses recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Survey not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      },
      summary = "Record Responses",
      description = "Handles POST requests to record responses from a respondent.")
  public ResponseEntity<ResponseManagementResponseDto> recordResponses(
      @Valid @RequestBody ResponseManagementDto request,
      @RequestParam(value = "respondentId", required = false) String respondentId) {
    return new ResponseEntity<>(
        responseManagementService.recordResponses(request, respondentId), HttpStatus.OK);
  }

  /**
   * Retrieves all survey templates.
   *
   * @return ResponseEntity containing all survey templates.
   */
  @GetMapping("/templates")
  @Operation(
      summary = "Get Templates",
      description = "Retrieves all survey templates.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Templates retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Templates not found")
      })
  public ResponseEntity<AllSurveysResponseDto> getTemplates(
      @RequestParam(value = "page") int page,
      @RequestParam(value = "limit") int limit,
      @RequestParam(value = "tab", required = false) String tab) {
    return new ResponseEntity<>(surveyService.getTemplate(page, limit, tab), HttpStatus.OK);
  }

  /**
   * Adds a collaborator to a survey.
   *
   * @param userId The ID of the user to add as a collaborator.
   * @param surveyId The ID of the survey.
   * @param token The invitation token.
   * @param canEdit Boolean indicating whether the collaborator can edit the survey.
   * @return ResponseEntity indicating the success or failure of the collaborator addition.
   */
  @GetMapping("/invite/{userId}")
  @Operation(
      responses = {
        @ApiResponse(responseCode = "200", description = "Collaborator added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "409", description = "Duplicate")
      },
      summary = "Add Collaborator",
      description = "Adds a collaborator to a survey with the specified permissions.")
  public RedirectView addCollaborator(
      @PathVariable String userId,
      @RequestParam String surveyId,
      @RequestParam String token,
      @RequestParam boolean canEdit) {
    try {
      VerifyInviteResponseDto verifyInviteResponseDto =
          invitationService.verifyInvite(userId, surveyId, token, canEdit);
      if (verifyInviteResponseDto.isVerified()) {
        String redirectUrl =
            Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN"))
                + "/survey_collaboration/"
                + verifyInviteResponseDto.getCollaborator().getHashedUserId()
                + "/"
                + surveyId;
        return new RedirectView(redirectUrl);
      } else {
        return new RedirectView(
            Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN"))
                .concat("/failed_Verification"));
      }
    } catch (DuplicateException e) {
      return new RedirectView(
          Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN"))
              .concat("/survey_collaboration/")
              .concat(generateSHA256(userId))
              .concat("/")
              .concat(surveyId));
    }
  }

  /**
   * Finds a collaborator of a survey.
   *
   * @param userId The ID of the user to find a collaborator.
   * @param surveyId The ID of the survey.
   * @return ResponseEntity indicating the success or failure of finding a collaborator.
   */
  @GetMapping("/collaborator")
  public ResponseEntity<Collaborators> findCollaborator(
      @RequestParam("userId") String userId, @RequestParam("surveyId") String surveyId) {
    return new ResponseEntity<>(
        invitationService.findCollaborator(userId, surveyId), HttpStatus.OK);
  }

  @GetMapping("/invite-admin/{userId}")
  public RedirectView verifyAdmin(@PathVariable("userId") String userId) {
    userService.verifyAdmin(userId);

    return new RedirectView(
        Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN")).concat("/login"));
  }

  private final AiService aiService;

//  @GetMapping("/create-survey-ai")
//  public Mono<Object> createSurveysAI(
//                                      @RequestParam String title,
//                                      @RequestParam String category,
//                                      @RequestParam String description,
//                                      @RequestParam String blocks,
//                                      @RequestParam String targetAudience,
//                                      @RequestParam String questions) {
//    return aiService.createSurveysAI(
//            title, category, description, blocks, targetAudience, questions);
//  }
}
