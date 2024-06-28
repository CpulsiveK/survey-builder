package com.amalitech.surveysphere.services.invitationService;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.*;
import static com.amalitech.surveysphere.enums.Endpoint.*;
import static com.amalitech.surveysphere.enums.Role.*;

import com.amalitech.surveysphere.dto.requestDto.InvitationDto;
import com.amalitech.surveysphere.dto.requestDto.InviteAdminDto;
import com.amalitech.surveysphere.dto.responseDto.InvitationResponseDto;
import com.amalitech.surveysphere.dto.responseDto.VerifyInviteResponseDto;
import com.amalitech.surveysphere.exceptions.DuplicateException;
import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.models.Collaborators;
import com.amalitech.surveysphere.models.Survey;
import com.amalitech.surveysphere.models.Token;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.InviteUserRepository;
import com.amalitech.surveysphere.repositories.SurveyRepository;
import com.amalitech.surveysphere.repositories.TokenRepository;
import com.amalitech.surveysphere.repositories.UserRepository;
import com.amalitech.surveysphere.services.otherServices.mailService.MailServiceImplementation;
import com.amalitech.surveysphere.services.tokenService.TokenService;
import com.google.common.hash.Hashing;
import jakarta.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Implementation class for handling invitations. */
@Service
@RequiredArgsConstructor
public class InvitationServiceImplementation implements InvitationService {
  private final UserRepository userRepository;
  private final SurveyRepository surveyRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final Environment env;
  private final MailServiceImplementation mailService;
  private final UserDetailsService userDetailsService;
  private final InviteUserRepository inviteUserRepository;

  @Value("${INVITATION_LINK}")
  private String INVITATION_LINK;

  /**
   * Sends an invitation to a user.
   *
   * @param request The InvitationDto object containing information for sending the invitation.
   * @return A ResponseEntity indicating the success or failure of the invitation sending operation.
   * @throws MessagingException if an error occurs while sending the invitation email.
   */
  @Override
  public InvitationResponseDto inviteUser(InvitationDto request) throws MessagingException {
    for (Map.Entry<String, Boolean> entry : request.getEmailEditMap().entrySet()) {
      String email = entry.getKey();
      Optional<User> user = userRepository.findByEmail(email);

      if (user.isEmpty()) {
        throw new NotFoundException(email + " is not a registered user");
      } else {
        String userId = user.get().getId();
        String sender = request.getSender();
        String surveyId = request.getSurveyId();
        boolean canEdit = entry.getValue();
        String link = env.getProperty("email.invitation.message");

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        var jwtToken = tokenService.generateToken(userDetails, userId);

        mailService.invitationEmail(email, jwtToken, sender, surveyId, canEdit, link, userId);
      }
    }
    return InvitationResponseDto.builder().successMessage("Invitation Sent Successfully").build();
  }

  @Override
  public VerifyInviteResponseDto verifyInvite(
      String userId, String surveyId, String token, boolean canEdit) throws NotFoundException {
    VerifyInviteResponseDto verifyInviteResponseDto = new VerifyInviteResponseDto();
    boolean result = false;
    boolean isVerified;

    Optional<Token> tokenExist = tokenRepository.findByUserId(userId);
    if (tokenExist.isPresent()) {
      isVerified = passwordEncoder.matches(token, tokenExist.get().getToken());
      verifyInviteResponseDto.setCollaborator(addCollaboratorToSurvey(userId, surveyId, canEdit));
      result = isVerified;
      verifyInviteResponseDto.setVerified(result);
    }
    return verifyInviteResponseDto;
  }

  @Override
  public Collaborators findCollaborator(String userId, String surveyId) {
    Optional<Collaborators> collaborator =
        inviteUserRepository.findByHashedUserIdAndSurveyId(userId, surveyId);
    if (collaborator.isPresent()) {
      return collaborator.get();
    }
    throw new NotFoundException(COLLABORATOR_NOT_FOUND.getMessage());
  }

  /**
   * Adds a collaborator to a survey after successful verification of invitation.
   *
   * @param userId The ID of the user being added as a collaborator.
   * @param surveyId The ID of the survey to which the user is being added as a collaborator.
   * @param canEdit A boolean indicating whether the user can edit the survey.
   */
  private Collaborators addCollaboratorToSurvey(String userId, String surveyId, boolean canEdit) {
    Optional<Survey> survey = surveyRepository.findById(surveyId);
    if (survey.isPresent()) {
      Optional<Collaborators> collaborator =
          inviteUserRepository.findByUserIdAndSurveyId(userId, surveyId);
      if (collaborator.isPresent()) {
        throw new DuplicateException("You are already a collaborator");
      }
      Collaborators newCollaborator = new Collaborators();
      newCollaborator.setHashedUserId(generateSHA256(userId));
      newCollaborator.setUserId(userId);
      newCollaborator.setSurveyId(surveyId);
      newCollaborator.setCanEdit(canEdit);
      inviteUserRepository.save(newCollaborator);

      Survey existingSurvey = survey.get();
      existingSurvey.getAddedUsersId().add(newCollaborator);

      surveyRepository.save(existingSurvey);

      Optional<User> user = userRepository.findById(userId);
      if (user.isPresent()) {
        User existingUser = user.get();
        existingUser.addSurveyId(existingSurvey);
        userRepository.save(existingUser);
      }
      return newCollaborator;
    } else {
      throw new NotFoundException(SURVEY_NOT_FOUND.getMessage());
    }
  }

  @Override
  public InvitationResponseDto inviteAdmin(InviteAdminDto userDto) throws MessagingException {

    String temporaryPassword = "@Admin123";

    Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());

    if (existingUser.isEmpty()) {
      User user =
          User.builder()
              .email(userDto.getEmail())
              .password(passwordEncoder.encode(temporaryPassword))
              .role(ADMIN.name())
              .build();

      User admin = userRepository.save(user);

      mailService.sendAdminInvite(
          userDto.getEmail(), temporaryPassword, BACKEND_URL.getUrl() + admin.getId());
    } else {
      User user = existingUser.get();

      if (user.getRole().equals(ADMIN.name()))
        throw new DuplicateException(USER_ALREADY_AN_ADMIN.getMessage());

      mailService.sendAdminInvite(
          userDto.getEmail(), temporaryPassword, BACKEND_URL.getUrl() + user.getId());
    }

    return InvitationResponseDto.builder().successMessage("Invitation sent successfully").build();
  }

  public static String generateSHA256(String id) {
    return Hashing.sha256().hashString((id), StandardCharsets.UTF_8).toString();
  }
}
