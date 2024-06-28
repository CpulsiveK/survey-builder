package com.amalitech.surveysphere.services.userService;

import com.amalitech.surveysphere.dto.requestDto.*;
import com.amalitech.surveysphere.dto.responseDto.*;
import com.amalitech.surveysphere.models.Survey;
import com.amalitech.surveysphere.models.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/** Service interface defining user-related operations. */
public interface UserService {
  /**
   * Registers a new user.
   *
   * @param request The UserDto object containing user information for registration.
   * @return UserResponseDto containing the response to the user registration request.
   * @throws MessagingException if an error occurs during messaging.
   */
  UserResponseDto registerUser(UserDto request) throws MessagingException;

  /**
   * Verifies a user's account.
   *
   * @param userId The ID of the user.
   * @param token The verification token.
   * @return boolean indicating whether the account verification was successful.
   */
  boolean verifyAccount(String userId, String token);

  /**
   * Initiates a request to reset the user's password.
   *
   * @param requestDto The PasswordResetRequestDto object containing password reset request
   *     information.
   * @return PasswordRequestResponseDto containing the response to the password reset request.
   * @throws MessagingException if an error occurs during messaging.
   */
  PasswordRequestResponseDto requestPasswordReset(PasswordResetRequestDto requestDto)
      throws MessagingException;

  /**
   * Verifies a password reset request.
   *
   * @param code The verification code.
   * @param userId The ID of the user.
   * @return PasswordRequestVerificationResponseDto containing the response to the password reset
   *     request verification.
   */
  PasswordRequestVerificationResponseDto verifyRequest(String code, String userId);

  /**
   * Resets the user's password.
   *
   * @param resetDto The ResetPasswordDto object containing new password information.
   * @param userId The ID of the user.
   * @return PasswordResetResponseDto containing the response to the password reset request.
   */
  PasswordResetResponseDto resetPassword(ResetPasswordDto resetDto, String userId);

  /**
   * Retrieves the authenticated user.
   *
   * @param httpServletRequest The HttpServletRequest object.
   * @return ResponseEntity containing the response to the authenticated user request.
   * @throws IOException if an I/O error occurs.
   * @throws MessagingException if an error occurs during messaging.
   */
  UserResponseDto getAuthenticatedUser(HttpServletRequest httpServletRequest)
      throws IOException, MessagingException;

  /**
   * Retrieves all users.
   *
   * @return List of users.
   */
  AllUsersResponseDto getUsers(int page, int limit);

  /**
   * Activates or deactivates users.
   *
   * @param request The AccountManagementDto object containing account management information.
   * @return User object representing the activated or deactivated user.
   */
  String activateAndDeactivateUsers(AccountManagementDto request);

  /**
   * Updates user information based on the provided UpdateUserDto.
   *
   * @param request The UpdateUserDto containing the information to update for the user.
   * @return The updated User object reflecting the changes made.
   */
  User updateUser(UpdateUserDto request);

  /**
   * Creates a template for a survey based on the provided SurveyDto.
   *
   * @param surveyDto The SurveyDto containing information to create the survey template.
   * @return A Map representing the created survey template, where keys are strings representing
   *     question IDs and values are strings representing corresponding question templates.
   */
  Survey createTemplate(SurveyDto surveyDto);

  /**
   * Updates the password for a user based on the provided ResetPasswordDto.
   *
   * @param resetPasswordDto The ResetPasswordDto containing the new password and other necessary
   *     information.
   * @return A PasswordResetResponseDto indicating the status of the password update operation.
   */
  PasswordResetResponseDto updatePassword(ResetPasswordDto resetPasswordDto);

  /**
   * Verifies if the user with the given user ID is an admin.
   *
   * @param userId The ID of the user to be verified
   */
  void verifyAdmin(String userId);
}
