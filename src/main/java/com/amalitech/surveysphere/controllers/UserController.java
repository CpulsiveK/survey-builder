package com.amalitech.surveysphere.controllers;

import com.amalitech.surveysphere.dto.requestDto.InvitationDto;
import com.amalitech.surveysphere.dto.requestDto.ResetPasswordDto;
import com.amalitech.surveysphere.dto.requestDto.UpdateUserDto;
import com.amalitech.surveysphere.dto.responseDto.*;
import com.amalitech.surveysphere.models.Collaborators;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.services.invitationService.InvitationService;
import com.amalitech.surveysphere.services.otherServices.excelService.ExcelService;
import com.amalitech.surveysphere.services.surveyService.SurveyService;
import com.amalitech.surveysphere.services.userService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controller class for handling user-related operations in the Survey Sphere application.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey-sphere/user")
@Tag(name = "User", description = "Operations related to users")
@SecurityRequirement(name = "emailPasswordAuth")
public class UserController {
    private final UserService userService;
    private final InvitationService invitationService;

    /**
     * Sends an invitation to a user.
     *
     * @param invitationDto The InvitationDto object containing invitation information.
     * @return ResponseEntity indicating the success or failure of the invitation sending process.
     * @throws MessagingException if an error occurs during email sending.
     */
    @PostMapping("/send-invite")
    @Operation(
            summary = "Send Invite",
            description = "Sends an invitation to a user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitation sent successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<?> sendInvite(@RequestBody InvitationDto invitationDto)
            throws MessagingException {
        return new ResponseEntity<>(invitationService.inviteUser(invitationDto), HttpStatus.OK);
    }

    /**
     * Adds a collaborator to a survey.
     *
     * @param userId   The ID of the user to add as a collaborator.
     * @param surveyId The ID of the survey.
     * @param token    The invitation token.
     * @param canEdit  Boolean indicating whether the collaborator can edit the survey.
     * @return ResponseEntity indicating the success or failure of the collaborator addition.
     */
    @PostMapping("/invite/{userId}")
    @Operation(
            responses = {
                    @ApiResponse(responseCode = "200", description = "Collaborator added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            summary = "Add Collaborator",
            description = "Adds a collaborator to a survey with the specified permissions.")
    public ResponseEntity<Boolean> addCollaborator(
            @PathVariable String userId,
            @RequestParam String surveyId,
            @RequestParam String token,
            @RequestParam boolean canEdit) {
        VerifyInviteResponseDto verifyInviteResponseDto =
                invitationService.verifyInvite(userId, surveyId, token, canEdit);
        return new ResponseEntity<>(
                verifyInviteResponseDto.isVerified(), HttpStatus.OK);
    }

    /**
     * Retrieves the authenticated user.
     *
     * @param httpServletRequest The HttpServletRequest object.
     * @return ResponseEntity containing the authenticated user.
     * @throws IOException        if an I/O error occurs.
     * @throws MessagingException if an error occurs during email sending.
     */
    @GetMapping("/get-authenticated-user")
    @Operation(
            summary = "Get Authenticated User",
            description = "Retrieves the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authenticated user retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<UserResponseDto> getAuthenticated(HttpServletRequest httpServletRequest)
            throws IOException, MessagingException {
        return new ResponseEntity<>(
                userService.getAuthenticatedUser(httpServletRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Update user",
            description = "Allows user to update their profile",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User model returned"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PutMapping("/update-user")
    public ResponseEntity<User> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        return new ResponseEntity<>(userService.updateUser(updateUserDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Update user password",
            description = "Allows user to update existing password",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password reset response dto returned"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PutMapping("/update-password")
    public ResponseEntity<PasswordResetResponseDto> updatePassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return new ResponseEntity<>(userService.updatePassword(resetPasswordDto), HttpStatus.OK);
    }
}
