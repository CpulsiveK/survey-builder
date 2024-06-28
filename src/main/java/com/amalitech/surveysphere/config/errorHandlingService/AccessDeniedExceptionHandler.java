package com.amalitech.surveysphere.config.errorHandlingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * AccessDeniedExceptionHandler is a class that implements the AccessDeniedHandler interface from
 * Spring Security. It is responsible for handling access denied situations, where a user is not
 * authorized to access a specific resource.
 */
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {
  /**
   * Handles the access denied situations.
   *
   * @param request the HTTP request that resulted in the access denial
   * @param response the HTTP response to be populated with the error details
   * @param accessDeniedException the exception that caused the access denial
   * @throws IOException if an I/O error occurs during the handling of the access denial
   * @throws ServletException if a servlet-specific error occurs during the handling of the access
   *     denial
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
  }
}
