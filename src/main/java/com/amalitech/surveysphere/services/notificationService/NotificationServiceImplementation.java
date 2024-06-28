package com.amalitech.surveysphere.services.notificationService;

import com.amalitech.surveysphere.dto.responseDto.DistributionResponseDto;
import com.amalitech.surveysphere.services.otherServices.mailService.MailServiceImplementation;
import jakarta.mail.MessagingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImplementation implements NotificationService {
  private final MailServiceImplementation mailService;
  @Override
  public  DistributionResponseDto sendNotificationEmails(List<String> emails, String subject, String message) throws MessagingException {

      for (String email : emails) {
        mailService.sendNotificationEmail(email, subject, message);
      }
      return
              DistributionResponseDto.builder().message("Email Sent Successfully").build();
    }
}
