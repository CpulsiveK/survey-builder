package com.amalitech.surveysphere.services.socialUserService;

import com.amalitech.surveysphere.config.authenticationProvider.EmailAuthenticationToken;
import com.amalitech.surveysphere.dto.requestDto.JwtDto;
import com.amalitech.surveysphere.dto.requestDto.SocialLoginDto;
import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
import com.amalitech.surveysphere.enums.SocialLoginProvider;
import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.exceptions.UnauthorizedException;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.UserRepository;
import com.amalitech.surveysphere.services.responseService.UserResponseService;
import com.amalitech.surveysphere.services.socialUserService.socialProviderverificationService.JwtVerificatonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Implementation class for managing social user services. */
@Service
@RequiredArgsConstructor
public class SocialUserServiceImplementation implements SocialUserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final UserResponseService userResponseService;
  private static final SocialLoginProvider DEFAULT_PROVIDER = SocialLoginProvider.GOOGLE;
  private final JwtVerificatonService jwtDecoderService;

  @Value("${SOCIAL_LOGIN_PATH}")
  private String SOCIAL_LOGIN_PATH;

  /**
   * Performs social login.
   *
   * @param authentication The Authentication object representing the user's authentication details.
   * @param jwtDto The JwtDto object containing JWT-related information.
   * @param httpServletRequest The HttpServletRequest object representing the HTTP request.
   * @return A User indicating the success of the social login operation.
   * @throws ParseException If an error occurs during parsing.
   * @throws GeneralSecurityException If a general security error occurs.
   * @throws IOException If an IO error occurs.
   */
  @Override
  public UserResponseDto socialLogin(
      Authentication authentication, JwtDto jwtDto, HttpServletRequest httpServletRequest)
      throws ParseException, GeneralSecurityException, IOException {

    SocialLoginDto socialUserDto = jwtDecoderService.decodeJwt(jwtDto.getCredential());

    User user = findSocialUserByEmail(socialUserDto);
    socialUserDto.setPassword(
        user.getPassword().equals(passwordEncoder.encode("")) ? "" : socialUserDto.getPassword());
    authenticateSocialUser(authentication, httpServletRequest, socialUserDto);
    return userResponseService.sendSuccessResponse(user, httpServletRequest);
  }

  /**
   * Finds a social user by email.
   *
   * @param socialUserDto The SocialLoginDto object containing the user's social login information.
   * @return The User object representing the social user found by email.
   */
  @Override
  public User findSocialUserByEmail(SocialLoginDto socialUserDto) {
    try {
      return userRepository
          .findByEmail(socialUserDto.getEmail())
          .map(
              user -> {
                if (!user.isEnabled()) {
                  throw new UnauthorizedException("Account Deactivated!!!");
                }
                return user;
              })
          .orElseGet(
              () -> {
                User user =
                    User.builder()
                        .username(socialUserDto.getUsername())
                        .email(socialUserDto.getEmail())
                        .role("USER")
                        .socialLogins(
                            Collections.singletonList(
                                getSocialLoginProvider(socialUserDto.getSocialProvider())))
                        .build();
                if (socialUserDto.getPassword() == null) {
                  user.setPassword(passwordEncoder.encode(""));
                } else {
                  user.setPassword(passwordEncoder.encode(socialUserDto.getPassword()));
                }
                return userRepository.save(user);
              });
    } catch (DataAccessException e) {
      throw new RuntimeException("Error occurred while saving user to the database", e);
    }
  }

  /**
   * Authenticates a social user.
   *
   * @param authentication The Authentication object representing the user's authentication details.
   * @param httpServletRequest The HttpServletRequest object representing the HTTP request.
   * @param socialLoginDto The SocialLoginDto object containing the user's social login information.
   * @throws RuntimeException If an error occurs during authentication.
   */
  @Override
  public void authenticateSocialUser(
      Authentication authentication,
      HttpServletRequest httpServletRequest,
      SocialLoginDto socialLoginDto)
      throws RuntimeException {
    Optional<User> userExists = userRepository.findByEmail(socialLoginDto.getEmail());
    if (userExists.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    EmailAuthenticationToken authRequest = new EmailAuthenticationToken(socialLoginDto.getEmail());

    authentication = authenticationManager.authenticate(authRequest);

    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(authentication);

    HttpSession session = httpServletRequest.getSession(true);

    session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);


    session.setMaxInactiveInterval((int) TimeUnit.DAYS.toSeconds(1));
  }

  /**
   * Retrieves the social login provider.
   *
   * @param provider The name of the social login provider.
   * @return The SocialLoginProvider object representing the social login provider.
   */
  @Override
  public SocialLoginProvider getSocialLoginProvider(String provider) {
    return SocialLoginProvider.findByProviderName(provider).orElse(DEFAULT_PROVIDER);
  }
}
