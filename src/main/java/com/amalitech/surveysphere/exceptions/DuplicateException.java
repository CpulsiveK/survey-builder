package com.amalitech.surveysphere.exceptions;

/** Exception class representing a duplicate entity error. */
public class DuplicateException extends RuntimeException {

  /**
   * Constructs a new DuplicateException with the specified error message.
   *
   * @param message The detail message of the exception.
   */
  public DuplicateException(String message) {
    super(message);
  }
}
