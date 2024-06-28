package com.amalitech.surveysphere.services.otherServices.mailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Objects;

/** Implementation of the MailService interface for sending emails asynchronously. */
@Service
@RequiredArgsConstructor
public class MailServiceImplementation implements MailService {

  private final JavaMailSender javaMailSender;
  private final Environment env;
  private final TemplateEngine templateEngine;
  private static final String USERNAME = "username";
  private static final String EMAIL = "email";
  private static final String SUBJECT = "subject";
  private static final String TEMPLATE = "template";
  private static final String ENCODING = "UTF-8";

  /**
   * Asynchronously sends an email.
   *
   * @param emailProperties The MimeMessage object representing the email to be sent.
   * @throws MessagingException If an error occurs during the email sending process.
   */
  private void sendEmail(Map<String, Object> emailProperties) throws MessagingException {
    Context context = new Context();

    for (Map.Entry<String, Object> entry : emailProperties.entrySet()) {
      if (!entry.getKey().equals(TEMPLATE)) {
        context.setVariable(entry.getKey(), entry.getValue());
      }
    }

    String templateName = (String) emailProperties.get(TEMPLATE);
    String mailContent = templateEngine.process(templateName, context);

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, ENCODING);

    helper.setTo((String) emailProperties.get(EMAIL));
    helper.setSubject((String) emailProperties.get(SUBJECT));
    helper.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.from.email")));
    helper.setText(mailContent, true);

    javaMailSender.send(mimeMessage);
  }

  @Override
  @Async
  public void sendAccountVerificationEmail(
      String email, String jwtToken, String message, String userId) throws MessagingException {
    Map<String, Object> emailProperties =
        Map.of(
            EMAIL,
            email,
            SUBJECT,
            "Account Verification",
            USERNAME,
            email,
            "verificationLink",
            message + userId + "?token=" + jwtToken,
            TEMPLATE,
            "AccountVerification");
    sendEmail(emailProperties);
  }

  public void passwordResetEmail(String email, String message) throws MessagingException {
    Map<String, Object> emailProperties =
        Map.of(
            EMAIL,
            email,
            SUBJECT,
            "Password Reset Request Verification",
            USERNAME,
            email,
            "code",
            message,
            TEMPLATE,
            "PasswordResetRequest");
    sendEmail(emailProperties);
  }

  @Override
  @Async
  public void invitationEmail(
      String email,
      String jwtToken,
      String sender,
      String surveyId,
      boolean canEdit,
      String message,
      String userId)
      throws MessagingException {
    Map<String, Object> emailProperties =
        Map.of(
            EMAIL,
            email,
            SUBJECT,
            "Survey Builder Invitation",
            "sender",
            sender,
            USERNAME,
            email,
            "invitationLink",
            message
                + userId
                + "?surveyId="
                + surveyId
                + "&token="
                + jwtToken
                + "&canEdit="
                + canEdit,
            TEMPLATE,
            "CollaborationInvitation");
    sendEmail(emailProperties);
  }

  @Override
  @Async
  public void sendSurvey(String email, String subject, String message, String surveyLink)
      throws MessagingException {
    Map<String, Object> emailProperties =
        Map.of(
            EMAIL,
            email,
            SUBJECT,
            subject,
            "message",
            message,
            "surveyLink",
            surveyLink,
            TEMPLATE,
            "SurveyEmail");
    sendEmail(emailProperties);
  }

  @Override
  @Async
  public void sendNotificationEmail(String email, String subject, String message)
      throws MessagingException {
    Map<String, Object> emailProperties =
        Map.of(EMAIL, email, SUBJECT, subject, "message", message, TEMPLATE, "NotificationEmail");
    sendEmail(emailProperties);
  }

  @Override
  public void sendAdminInvite(String email, String temporaryPassword, String link)
          throws MessagingException {
    if (email != null && temporaryPassword != null && link != null) {
      Map<String, Object> emailProperties =
              Map.of(
                      EMAIL,
                      email,
                      SUBJECT,
                      "Survey Builder Admin Invitation",
                      "password",
                      temporaryPassword,
                      "loginLink",
                      link,
                      TEMPLATE,
                      "AdminInvitation");
      sendEmail(emailProperties);
    } else {
      throw new IllegalArgumentException("One or more parameters are null.");
    }
  }
}
