package com.amalitech.surveysphere.exceptions;

/** Exception class representing an unauthorized access error. */
public class UnauthorizedException extends RuntimeException {

  /**
   * Constructs a new UnauthorizedException with the specified error message.
   *
   * @param message The detail message of the exception.
   */
  public UnauthorizedException(String message) {
    super(message);
  }
}
