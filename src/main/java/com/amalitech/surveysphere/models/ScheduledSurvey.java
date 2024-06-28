package com.amalitech.surveysphere.models;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ScheduledSurvey {
  @Id private String id;

  private LocalDateTime scheduledDate;

  private String surveyId;

  private String message;

  private String subject;

  private List<String> emails;

  private boolean isCompleted;

  private String action;
}
