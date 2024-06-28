package com.amalitech.surveysphere.config.authenticationProvider;

import com.amalitech.surveysphere.services.otherServices.userDetailsService.IUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/** Provides authentication based on email from social login. */
@Component
@RequiredArgsConstructor
public class EmailAuthenticationProvider implements AuthenticationProvider {
  private final IUserDetailsService userDetailsService;

  /**
   * Attempts to authenticate the user based on the provided authentication token.
   *
   * @param authentication the authentication token containing the user's credentials
   * @return an authenticated authentication token if successful
   * @throws AuthenticationException if authentication fails
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String email = authentication.getName();

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  /**
   * Indicates whether this authentication provider supports the given authentication token class.
   *
   * @param authentication the class to check for support
   * @return true if this authentication provider supports the given class, otherwise false
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return EmailAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
