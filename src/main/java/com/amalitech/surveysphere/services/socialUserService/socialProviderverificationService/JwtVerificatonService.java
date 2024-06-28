package com.amalitech.surveysphere.services.socialUserService.socialProviderverificationService;

import com.amalitech.surveysphere.dto.requestDto.SocialLoginDto;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

/** Interface for verifying JWT tokens. */
public interface JwtVerificatonService {
  /**
   * Decodes the JWT token and retrieves user information.
   *
   * @param jwt The JWT token to be decoded.
   * @return The SocialLoginDto object containing the decoded user information.
   * @throws ParseException If an error occurs during parsing.
   * @throws GeneralSecurityException If a general security error occurs.
   * @throws IOException If an IO error occurs.
   */
  SocialLoginDto decodeJwt(String jwt) throws ParseException, GeneralSecurityException, IOException;
}
