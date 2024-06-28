package com.amalitech.surveysphere.services.userService;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.*;
import static com.amalitech.surveysphere.enums.Role.*;

import com.amalitech.surveysphere.dto.requestDto.*;
import com.amalitech.surveysphere.dto.responseDto.*;
import com.amalitech.surveysphere.exceptions.DuplicateException;
import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.models.*;
import com.amalitech.surveysphere.repositories.*;
import com.amalitech.surveysphere.services.codeService.CodeService;
import com.amalitech.surveysphere.services.otherServices.mailService.MailServiceImplementation;
import com.amalitech.surveysphere.services.otherServices.uploadService.UploadService;
import com.amalitech.surveysphere.services.otherServices.userDetailsService.IUserDetailsService;
import com.amalitech.surveysphere.services.responseService.UserResponseService;
import com.amalitech.surveysphere.services.surveyService.SurveyService;
import com.amalitech.surveysphere.services.tokenService.TokenService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Service implementation class for handling user-related operations. */
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CodeRepository codeRepository;
  private final TokenService tokenService;
  private final CodeService codeService;
  private final Environment env;
  private final MailServiceImplementation mailService;
  private final TokenRepository tokenRepository;
  private final IUserDetailsService iUserDetailsService;
  private final UserResponseService userResponseService;
  private final RespondentRepository respondentRepository;
  private final SurveyService surveyService;
  private final UploadService uploadService;
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  /**
   * Registers a new user.
   *
   * @param request The UserDto object containing user information for registration.
   * @return UserResponseDto.
   * @throws MessagingException if an error occurs during messaging.
   */
  @Override
  public UserResponseDto registerUser(UserDto request) throws MessagingException {
    logger.trace("Attempting to register user: {}", request.getEmail());
    return createUser(request);
  }

  private UserResponseDto createUser(UserDto request) throws MessagingException {
    Optional<User> userExists;

    userExists = userRepository.findByEmail(request.getEmail());

    if (userExists.isPresent()) throw new DuplicateException(USER_ALREADY_REGISTERED.getMessage());

    Optional<Respondent> respondentExist = respondentRepository.findByEmail(request.getEmail());

    User user;

    if (respondentExist.isPresent()) {
      user =
          User.builder()
              .username(request.getUsername())
              .name(request.getName())
              .enabled(true)
              .email(toTrimmedLowerCase(request.getEmail()))
              .password(passwordEncoder.encode(toTrimmedLowerCase(request.getPassword())))
              .role(
                  (request.getRole() != null && (request.getRole().equals("ADMIN")))
                      ? ADMIN.name()
                      : USER.name())
              .build();
    } else {
      user =
          User.builder()
              .username(request.getUsername())
              .name(request.getName())
              .enabled(true)
              .email(toTrimmedLowerCase(request.getEmail()))
              .password(passwordEncoder.encode(toTrimmedLowerCase(request.getPassword())))
              .role(
                  (request.getRole() != null && (request.getRole().equals("ADMIN")))
                      ? ADMIN.name()
                      : USER.name())
              .build();
    }

    User created = userRepository.save(user);
    logger.trace("User created successfully by: {}", user.getCreatedBy());

    String userId = created.getId();

    String jwtToken =
        tokenService.generateToken(
            iUserDetailsService.loadUserByUsername(created.getEmail()), userId);

    mailService.sendAccountVerificationEmail(
        created.getEmail(), jwtToken, env.getProperty("email.verify.account.message"), userId);
    created.setEnabled(false);
    userRepository.save(created);
    return UserResponseDto.builder()
        .id(created.getId())
        .email(created.getEmail())
        .role(created.getRole())
        .respondent(created.getRespondent())
        .build();
  }

  /**
   * Verifies a user's account.
   *
   * @param userId The ID of the user.
   * @param token The verification token.
   * @return boolean indicating whether the account verification was successful.
   */
  @Override
  public boolean verifyAccount(String userId, String token) {
    return tokenRepository
        .findByUserId(userId)
        .filter(tokenEntity -> passwordEncoder.matches(token, tokenEntity.getToken()))
        .flatMap(tokenEntity -> userRepository.findById(userId))
        .map(
            existingUser -> {
              existingUser.setVerified(true);
              existingUser.setEnabled(true);
              userRepository.save(existingUser);
              return true;
            })
        .orElse(false);
  }

  /**
   * Initiates a request to reset the user's password.
   *
   * @param requestDto The PasswordResetRequestDto object containing password reset request
   *     information.
   * @return PasswordRequestResponseDto containing the response to the password reset request.
   * @throws MessagingException if an error occurs during messaging.
   */
  @Override
  public PasswordRequestResponseDto requestPasswordReset(PasswordResetRequestDto requestDto)
      throws MessagingException {
    Optional<User> userExists = userRepository.findByEmail(requestDto.getEmail());

    if (userExists.isEmpty()) throw new NotFoundException("User not found");
    var userId = userExists.get().getId();
    var email = userExists.get().getEmail();

    var code = codeService.generateCode(userId);

    mailService.passwordResetEmail(email, code);

    return PasswordRequestResponseDto.builder().userId(userId).build();
  }

  /**
   * Verifies a password reset request.
   *
   * @param code The verification code.
   * @param userId The ID of the user.
   * @return PasswordRequestVerificationResponseDto containing the response to the password reset
   *     request verification.
   */
  @Override
  public PasswordRequestVerificationResponseDto verifyRequest(String code, String userId) {
    Code codeExist = codeRepository.findByUserId(userId);
    boolean codeMatches = passwordEncoder.matches(code, codeExist.getCode());
    return PasswordRequestVerificationResponseDto.builder().verificationStatus(codeMatches).build();
  }

  /**
   * Resets the user's password.
   *
   * @param resetDto The ResetPasswordDto object containing new password information.
   * @param userId The ID of the user.
   * @return PasswordResetResponseDto containing the response to the password reset request.
   */
  @Override
  public PasswordResetResponseDto resetPassword(ResetPasswordDto resetDto, String userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isPresent()) {
      User existingUser = user.get();
      String newPassword = toTrimmedLowerCase(resetDto.getPassword());

      if (passwordEncoder.matches(newPassword, existingUser.getPassword()))
        throw new DuplicateException(SAME_PASSWORD.getMessage());

      existingUser.setPassword(passwordEncoder.encode(newPassword));
      userRepository.save(existingUser);

      existingUser.setPassword(passwordEncoder.encode(resetDto.getPassword()));
      userRepository.save(existingUser);
      return PasswordResetResponseDto.builder().successMessage("Password Reset Successful").build();
    } else {
      return PasswordResetResponseDto.builder()
          .successMessage("Password Reset Unsuccessful")
          .build();
    }
  }

  /**
   * Retrieves the authenticated user.
   *
   * @param httpServletRequest The HttpServletRequest object.
   * @return UserResponseDto
   * @throws IOException if an I/O error occurs.
   */
  @Override
  public UserResponseDto getAuthenticatedUser(HttpServletRequest httpServletRequest)
      throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Optional<User> userExists = userRepository.findByEmail(authentication.getName());

    if (userExists.isPresent()) {
      User userFound = userExists.get();
      return userResponseService.sendSuccessResponse(userFound, httpServletRequest);
    }
    return null;
  }

  /**
   * Retrieves all users.
   *
   * @return List of users.
   */
  @Override
  public AllUsersResponseDto getUsers(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<User> users = userRepository.findAllByDeletedAndRoleNot(true, ADMIN.name(), pageable);

    return AllUsersResponseDto.builder()
        .users(users.getContent())
        .totalPages(users.getTotalPages())
        .build();
  }

  /**
   * Activates or deactivates users.
   *
   * @param request The AccountManagementDto object containing account management information.
   * @return User object representing the activated or deactivated user.
   */
  @Override
  public String activateAndDeactivateUsers(AccountManagementDto request) {
    User user =
        userRepository
            .findById(request.getId())
            .orElseThrow(() -> new NotFoundException(USER_NOT_AUTHENTICATED.getMessage()));
    switch (request.getStatus()) {
      case "ACTIVATE":
        user.setEnabled(true);
        break;
      case "DEACTIVATE":
        user.setEnabled(false);
        break;
      case "DELETE":
        user.setDeleted(false);
        break;
      case "RESTORE":
        user.setDeleted(true);
        break;
      default:
        throw new IllegalArgumentException(INVALID_ACTIVATION_TYPE.getMessage());
    }
    userRepository.save(user);
    return "user updated successfully";
  }

  /**
   * Updates user information based on the provided UpdateUserDto.
   *
   * @param request The UpdateUserDto object containing information for updating the user.
   * @return The updated User object after the user information has been successfully updated.
   * @throws IllegalArgumentException If no user is found with the specified ID in the
   *     UpdateUserDto.
   * @throws RuntimeException If there is a failure while saving the updated user to the database.
   * @see UpdateUserDto
   */
  @Override
  public User updateUser(UpdateUserDto request) {
    Optional<User> existingUserOptional = userRepository.findById(request.getUserId());

    if (existingUserOptional.isEmpty()) throw new NotFoundException(USER_NOT_FOUND.getMessage());

    User existingUser = existingUserOptional.get();

    if (request.getProfilePicture() != null) {
      existingUser.setProfilePicture(uploadService.uploadFile(request.getProfilePicture()));
    }
    existingUser.setEmail(toTrimmedLowerCase(request.getEmail()));
    existingUser.setUsername(request.getUsername());

    return userRepository.save(existingUser);
  }

  /**
   * Creates a template for a survey based on the provided SurveyDto. This method delegates the
   * creation process to the surveyService.
   *
   * @param surveyDto The SurveyDto containing information to create the survey template.
   * @return A Map representing the created survey template, where keys are strings representing
   *     question IDs and values are strings representing corresponding question templates.
   */
  @Override
  public Survey createTemplate(SurveyDto surveyDto) {
    return surveyService.createSurvey(surveyDto);
  }

  /**
   * Updates the password of the authenticated user.
   *
   * @param value The string to be converted to lower case and trimmed.
   * @return Converted string value.
   */
  private String toTrimmedLowerCase(String value) {
    if (value == null) {
      return null;
    }
    return value.trim().toLowerCase();
  }

  /**
   * Updates the password of the authenticated user.
   *
   * @param resetPasswordDto The ResetPasswordDto object containing the new password information.
   * @return PasswordResetResponseDto containing the response to the password reset request.
   */
  @Override
  public PasswordResetResponseDto updatePassword(ResetPasswordDto resetPasswordDto) {
    logger.trace("Updating password for user");
    String userId = surveyService.getUserId();
    return resetPassword(resetPasswordDto, userId);
  }

  /**
   * Verifies if the user with the given user ID is an admin.
   *
   * @param userId The ID of the user to be verified
   */
  @Override
  public void verifyAdmin(String userId) {
    Optional<User> userExist = userRepository.findById(userId);

    if (userExist.isEmpty()) throw new NotFoundException(USER_NOT_FOUND.getMessage());

    User user = userExist.get();

    if (user.getRole().equals(ADMIN.name())) return;

    user.setRole(ADMIN.name());
    user.setPassword(passwordEncoder.encode("@Admin123"));
    userRepository.save(user);
  }
}
