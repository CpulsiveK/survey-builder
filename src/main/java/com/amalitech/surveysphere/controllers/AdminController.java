package com.amalitech.surveysphere.controllers;

import com.amalitech.surveysphere.dto.requestDto.*;
import com.amalitech.surveysphere.dto.responseDto.*;
import com.amalitech.surveysphere.models.Survey;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.services.dashboardService.DashboardService;
import com.amalitech.surveysphere.services.invitationService.InvitationService;
import com.amalitech.surveysphere.services.surveyService.SurveyService;
import com.amalitech.surveysphere.services.userService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Controller class for handling admin-related operations in the Survey Sphere application. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey-sphere/public")
@Tag(name = "Admin", description = "Operations related to admin")
@SecurityRequirement(name = "emailPasswordAuth")
@Validated
public class AdminController {
  private final UserService userService;
  private final SurveyService surveyService;
  private final InvitationService invitationService;
  private final DashboardService dashboardService;

  /**
   * Retrieves a list of users.
   *
   * @return ResponseEntity containing the list of users.
   */
  @GetMapping("/get-users")
  @Operation(
      summary = "Get Users",
      description = "Retrieves a list of users.",
      responses = {
        @ApiResponse(responseCode = "200", description = "All users retrieved successfully"),
      })
  public ResponseEntity<AllUsersResponseDto> getUsers(
      @RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) {
    return new ResponseEntity<>(userService.getUsers(page, limit), HttpStatus.OK);
  }

//  /**
//   * Registers a new admin.
//   *
//   * @param userDto The UserDto object containing user information for registration.
//   * @return ResponseEntity indicating the success or failure of the registration process.
//   * @throws MessagingException if an error occurs during email sending.
//   */
//  @PostMapping("/signup")
//  @Operation(
//      summary = "Register Admin",
//      description = "Registers a new admin.",
//      responses = {
//        @ApiResponse(responseCode = "201", description = "Admin registered successfully"),
//        @ApiResponse(responseCode = "400", description = "Bad request"),
//        @ApiResponse(responseCode = "409", description = "Admin already exists")
//      })
//  public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserDto userDto)
//      throws MessagingException {
//    return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
//  }

  /**
   * Handles HTTP PATCH requests to flag or unflag a survey.
   *
   * @param managementDto The AccountManagementDto containing the survey ID and flag status.
   * @return ResponseEntity containing the updated survey or an error response.
   */
  @PatchMapping("/flag-survey")
  @Operation(
      summary = "Flag Survey",
      description = "Flags a survey.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Survey flagged successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Survey not found")
      })
  public ResponseEntity<com.amalitech.surveysphere.models.Survey> flagSurvey(
      @Valid @RequestBody AccountManagementDto managementDto) {
    return new ResponseEntity<>(
        surveyService.activateOrDeactivateSurvey(managementDto), HttpStatus.OK);
  }

  /**
   * Manages user account activation status.
   *
   * @param managementDto The AccountManagementDto object containing user account management
   *     request.
   * @return ResponseEntity indicating the success or failure of the account management operation.
   */
  @PatchMapping("/account-management")
  @Operation(summary = "Manage Account", description = "Manages user account activation status.")
  public ResponseEntity<String> manageAccount(
      @Valid @RequestBody AccountManagementDto managementDto) {
    return new ResponseEntity<>(
        userService.activateAndDeactivateUsers(managementDto), HttpStatus.OK);
  }

  /**
   * Updates a user.
   *
   * @param updateUserDto The UpdateUserDto containing the updated user details.
   * @return ResponseEntity containing the updated user or an error response.
   */
  @PostMapping("/update-user")
  @Operation(
      summary = "Update User",
      description = "Updates a user.",
      responses = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public ResponseEntity<User> updateUser(@Valid @RequestBody UpdateUserDto updateUserDto) {
    return new ResponseEntity<>(userService.updateUser(updateUserDto), HttpStatus.OK);
  }

  @PostMapping("/templates")
  @Operation(
      summary = "add templates",
      description = "Add survey templates",
      responses = {
        @ApiResponse(responseCode = "201", description = "Survey template created successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
      })
  public ResponseEntity<Survey> createTemplate(@Valid @RequestBody SurveyDto surveyDto) {
    return new ResponseEntity<>(userService.createTemplate(surveyDto), HttpStatus.CREATED);
  }

  /**
   * Retrieves all active surveys.
   *
   * @return ResponseEntity containing active surveys.
   */
  @GetMapping("/user-surveys")
  @Operation(
      summary = "Get All Surveys",
      description = "Retrieves all active surveys.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "All active surveys retrieved successfully"),
      })
  public ResponseEntity<AllSurveysResponseDto> getActiveSurveys(
      @RequestParam(value = "page") int page,
      @RequestParam(value = "limit") int limit,
      @RequestParam(value = "uuid") String uuid) {
    return new ResponseEntity<>(surveyService.getActiveSurveys(page, limit, uuid), HttpStatus.OK);
  }

  /**
   * Deletes a specific survey.
   *
   * @param surveyId The ID of the survey to be deleted
   * @return ResponseEntity containing the deletion response
   */
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

  /**
   * Invites an admin.
   *
   * @param inviteAdminDto The DTO containing invite admin details
   * @return ResponseEntity containing the invitation response
   * @throws MessagingException Thrown if an error occurs while sending the invitation email
   */
  @PostMapping("/invite")
  public ResponseEntity<InvitationResponseDto> inviteAdmin(
      @Valid @RequestBody InviteAdminDto inviteAdminDto) throws MessagingException {
    return new ResponseEntity<>(invitationService.inviteAdmin(inviteAdminDto), HttpStatus.OK);
  }

  /**
   * Retrieves dashboard data.
   *
   * @return DashboardDto containing the dashboard data
   */
  @GetMapping("/data")
  public DashboardDto getDashboardData() {
    return dashboardService.getDashboardData();
  }
}
