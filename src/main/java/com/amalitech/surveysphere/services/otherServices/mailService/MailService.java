package com.amalitech.surveysphere.services.otherServices.mailService;


import jakarta.mail.MessagingException;
import java.util.Map;
import org.springframework.scheduling.annotation.Async;

/**
 * Interface for sending emails.
 */
public interface MailService {

    void sendAccountVerificationEmail(String email, String jwtToken, String message, String userId) throws MessagingException;
    void passwordResetEmail(String email, String message) throws MessagingException;
    void invitationEmail(String email, String jwtToken, String sender, String surveyId,boolean canEdit, String message, String userId) throws MessagingException;
    void sendSurvey(String email, String subject, String message, String surveyLink) throws MessagingException;
    void sendNotificationEmail(String email, String subject, String message) throws MessagingException;
    void sendAdminInvite(String email, String temporaryPassword, String link) throws MessagingException;
}
