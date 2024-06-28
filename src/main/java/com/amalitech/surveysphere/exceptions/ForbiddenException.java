package com.amalitech.surveysphere.exceptions;

/** Exception class representing a forbidden access error. */
public class ForbiddenException extends RuntimeException {
  /**
   * Constructs a new ForbiddenException with the specified error message.
   *
   * @param message The detail message of the exception.
   */
  public ForbiddenException(String message) {
    super(message);
  }
}
