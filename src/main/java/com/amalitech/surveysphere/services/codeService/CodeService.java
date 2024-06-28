package com.amalitech.surveysphere.services.codeService;

/** Interface for generating codes. */
public interface CodeService {
  /**
   * Generates a code for the specified user ID.
   *
   * @param userId The ID of the user for whom the code will be generated.
   * @return The generated code as a String.
   */
  String generateCode(String userId);
}
