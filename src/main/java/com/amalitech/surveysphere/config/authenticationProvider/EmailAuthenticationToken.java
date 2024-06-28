package com.amalitech.surveysphere.config.authenticationProvider;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class EmailAuthenticationToken extends UsernamePasswordAuthenticationToken {
  public EmailAuthenticationToken(Object principal) {
    super(principal, null);
  }
}
