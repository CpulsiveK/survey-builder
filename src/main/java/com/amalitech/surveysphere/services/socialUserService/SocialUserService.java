package com.amalitech.surveysphere.services.socialUserService;

import com.amalitech.surveysphere.dto.requestDto.JwtDto;
import com.amalitech.surveysphere.dto.requestDto.SocialLoginDto;
import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.enums.SocialLoginProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

/** Interface for managing social user services. */
public interface SocialUserService {

  /**
   * Performs social login.
   *
   * @param authentication The Authentication object representing the user's authentication details.
   * @param jwtDto The JwtDto object containing JWT-related information.
   * @param httpServletRequest The HttpServletRequest object representing the HTTP request.
   * @return A USer indicating the success of the social login operation.
   * @throws ParseException If an error occurs during parsing.
   * @throws GeneralSecurityException If a general security error occurs.
   * @throws IOException If an IO error occurs.
   */
  UserResponseDto socialLogin(
      Authentication authentication, JwtDto jwtDto, HttpServletRequest httpServletRequest)
      throws ParseException, GeneralSecurityException, IOException;

  /**
   * Finds a social user by email.
   *
   * @param socialUserDto The SocialLoginDto object containing the user's social login information.
   * @return The User object representing the social user found by email.
   */
  User findSocialUserByEmail(SocialLoginDto socialUserDto);

  /**
   * Authenticates a social user.
   *
   * @param authentication The Authentication object representing the user's authentication details.
   * @param httpServletRequest The HttpServletRequest object representing the HTTP request.
   * @param socialLoginDto The SocialLoginDto object containing the user's social login information.
   */
  void authenticateSocialUser(
      Authentication authentication,
      HttpServletRequest httpServletRequest,
      SocialLoginDto socialLoginDto);

  /**
   * Retrieves the social login provider.
   *
   * @param provider The name of the social login provider.
   * @return The SocialLoginProvider object representing the social login provider.
   */
  SocialLoginProvider getSocialLoginProvider(String provider);
}
