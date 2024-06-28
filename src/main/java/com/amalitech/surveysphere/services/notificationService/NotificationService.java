package com.amalitech.surveysphere.services.notificationService;

import com.amalitech.surveysphere.dto.responseDto.DistributionResponseDto;
import jakarta.mail.MessagingException;

import java.util.List;

/** Service interface for handling notifications such as sending emails. */
public interface NotificationService {

  /**
   * Sends emails to the specified email addresses with the given subject and message.
   *
   * @param emails The list of email addresses to send the emails to.
   * @param subject The subject of the email.
   * @param message The body of the email.
   * @return A DistributionResponseDto indicating the result of email distribution.
   */
  DistributionResponseDto sendNotificationEmails(List<String> emails, String subject, String message)
      throws MessagingException;
}
