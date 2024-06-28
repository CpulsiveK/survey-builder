package com.amalitech.surveysphere.services.invitationService;

import com.amalitech.surveysphere.dto.requestDto.InvitationDto;
import com.amalitech.surveysphere.dto.requestDto.InviteAdminDto;
import com.amalitech.surveysphere.dto.responseDto.InvitationResponseDto;
import com.amalitech.surveysphere.dto.responseDto.VerifyInviteResponseDto;
import com.amalitech.surveysphere.models.Collaborators;
import jakarta.mail.MessagingException;

/**
 * Interface for handling invitations.
 */
public interface InvitationService {

    /**
     * Sends an invitation to a user.
     *
     * @param request The InvitationDto object containing information for sending the invitation.
     * @return A ResponseEntity indicating the success or failure of the invitation sending operation.
     * @throws MessagingException if an error occurs while sending the invitation email.
     */
    InvitationResponseDto inviteUser(InvitationDto request) throws MessagingException;

    /**
     * Verifies an invitation.
     *
     * @param userId   The ID of the user to whom the invitation is sent.
     * @param surveyId The ID of the survey associated with the invitation.
     * @param token    The verification token received in the invitation.
     * @param canEdit  A boolean indicating whether the user can edit the survey.
     * @return A ResponseEntity containing the result of the invitation verification.
     */
    VerifyInviteResponseDto verifyInvite(String userId, String surveyId, String token, boolean canEdit);

    /**
     * Finds a collaborator.
     *
     * @param userId   The ID of the user to whom the invitation is sent.
     * @param surveyId The ID of the survey associated with the invitation.
     * @return A ResponseEntity containing the result of the invitation verification.
     */
    Collaborators findCollaborator(String hashedUserId, String surveyId);

    InvitationResponseDto inviteAdmin(InviteAdminDto inviteAdminDto) throws MessagingException;
}
