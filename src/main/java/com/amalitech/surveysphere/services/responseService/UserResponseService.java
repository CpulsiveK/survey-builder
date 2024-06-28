package com.amalitech.surveysphere.services.responseService;

import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
import com.amalitech.surveysphere.models.User;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/** Interface for providing response-related services for users. */
public interface UserResponseService {

  /**
   * Sends a success response with user details.
   *
   * @param user The user whose details will be included in the response.
   * @param httpServletRequest The HTTP request associated with the response.
   * @return A UserResponseDto representing the success response.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  UserResponseDto sendSuccessResponse(User user, HttpServletRequest httpServletRequest)
      throws IOException;
}
