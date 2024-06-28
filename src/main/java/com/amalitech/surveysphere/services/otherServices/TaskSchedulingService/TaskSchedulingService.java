package com.amalitech.surveysphere.services.otherServices.TaskSchedulingService;

import jakarta.mail.MessagingException;

/** Interface for managing scheduled tasks related to surveys. */
public interface TaskSchedulingService {
  /**
   * Distributes scheduled surveys.
   *
   * @throws MessagingException if an error occurs during email distribution.
   */
  void distributeScheduledSurvey() throws MessagingException;

  /** Deletes scheduled surveys. */
  void deleteScheduledSurvey();

  /** Archives scheduled surveys. */
  void archiveScheduledSurvey();
}
