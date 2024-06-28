package com.amalitech.surveysphere.exceptions;

/** Exception class representing a not found error. */
public class NotFoundException extends RuntimeException {

  /**
   * Constructs a new NotFoundException with the specified error message.
   *
   * @param message The detail message of the exception.
   */
  public NotFoundException(String message) {
    super(message);
  }
}
