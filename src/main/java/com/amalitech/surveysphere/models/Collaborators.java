package com.amalitech.surveysphere.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "collaborators")
public class Collaborators {
  @Id private String id;

  private String userId;

  private String surveyId;

  private String hashedUserId;

  private String hashedSurveyId;

  private Boolean canEdit;

  @CreatedBy
  String createdBy;

  @CreatedDate
  Date createdDate;

  @LastModifiedDate
  Date lastModifiedDate;

  @LastModifiedBy
  String lastModifiedBy;
}
