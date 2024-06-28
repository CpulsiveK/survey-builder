package com.amalitech.surveysphere.services.otherServices.TaskSchedulingService;

import com.amalitech.surveysphere.models.ScheduledSurvey;
import com.amalitech.surveysphere.models.Survey;
import com.amalitech.surveysphere.repositories.ScheduledSurveyRepository;
import com.amalitech.surveysphere.repositories.SurveyRepository;
import com.amalitech.surveysphere.services.otherServices.mailService.MailServiceImplementation;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Implementation of TaskSchedulingService for managing scheduled tasks related to surveys. */
@Component
@RequiredArgsConstructor
public class TaskSchedulingServiceImplementation implements TaskSchedulingService {
  private final ScheduledSurveyRepository scheduledSurveyRepository;
  private final MailServiceImplementation mailService;
  private final SurveyRepository surveyRepository;
  private static final int DELAY = 10000;

  /** Distributes scheduled surveys. */
  @Scheduled(initialDelay = 5000, fixedDelay = DELAY)
  @Override
  public void distributeScheduledSurvey() throws MessagingException {
    List<ScheduledSurvey> scheduledSurveys =
        scheduledSurveyRepository.findAllByActionAndIsCompletedAndScheduledDateBefore(
            "distribute", false, LocalDateTime.now());

    if (scheduledSurveys.isEmpty()) return;

    List<ScheduledSurvey> validSurveys =
        scheduledSurveys.stream()
            .filter(
                scheduledSurvey -> {
                  Survey survey =
                      surveyRepository.findById(scheduledSurvey.getSurveyId()).orElse(null);
                  return survey != null
                      && !survey.isDeleted()
                      && !scheduledSurvey.getEmails().isEmpty();
                })
            .toList();

    for (ScheduledSurvey scheduledSurvey : validSurveys) {
      Survey survey = surveyRepository.findById(scheduledSurvey.getSurveyId()).get();

      for (String email : scheduledSurvey.getEmails()) {
        mailService.sendSurvey(
            email,
            scheduledSurvey.getSubject(),
            scheduledSurvey.getMessage(),
            survey.getSurveyLink());
      }

      scheduledSurvey.setCompleted(true);
      survey.setSent(true);
      scheduledSurveyRepository.save(scheduledSurvey);
      surveyRepository.save(survey);
    }
  }

  /** Deletes scheduled surveys. */
  @Scheduled(initialDelay = 5000, fixedDelay = DELAY)
  @Override
  public void deleteScheduledSurvey() {
    List<ScheduledSurvey> scheduledSurveys =
        scheduledSurveyRepository.findAllByActionAndIsCompletedAndScheduledDateBefore(
            "delete", false, LocalDateTime.now());

    if (scheduledSurveys.isEmpty()) return;

    scheduledSurveys.forEach(
        scheduledSurvey -> {
          Optional<Survey> surveyExists = surveyRepository.findById(scheduledSurvey.getSurveyId());

          if (surveyExists.isEmpty()) return;

          Survey survey = surveyExists.get();

          if (survey.isDeleted()) return;

          survey.setDeleted(true);
          surveyRepository.save(survey);

          scheduledSurvey.setCompleted(true);
          scheduledSurveyRepository.save(scheduledSurvey);
        });
  }

  /** Archives scheduled surveys. */
  @Scheduled(initialDelay = 5000, fixedDelay = DELAY)
  @Override
  public void archiveScheduledSurvey() {
    List<ScheduledSurvey> scheduledSurveys =
        scheduledSurveyRepository.findAllByActionAndIsCompletedAndScheduledDateBefore(
            "archive", false, LocalDateTime.now());

    if (scheduledSurveys.isEmpty()) return;

    scheduledSurveys.forEach(
        scheduledSurvey -> {
          Optional<Survey> surveyExists = surveyRepository.findById(scheduledSurvey.getSurveyId());

          if (surveyExists.isEmpty()) return;

          Survey survey = surveyExists.get();

          if (survey.isDeleted()) return;

          survey.setArchived(true);
          surveyRepository.save(survey);
          scheduledSurvey.setCompleted(true);
          scheduledSurveyRepository.save(scheduledSurvey);
        });
  }
}
