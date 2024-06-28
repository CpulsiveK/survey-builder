package com.amalitech.surveysphere.models;

import com.amalitech.surveysphere.dto.requestDto.FileUploadDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "survey")
public class Survey {
  @Id private String id;

  private FileUploadDto logo;

  private SurveyTitle surveyTitle;

  private ColorScheme colorScheme;

  private String surveyLink;

  private String surveyOwner;

  private String category;

  private long respondentCount;

  @Builder.Default private String surveyView = "classic";

  @Builder.Default private boolean sent = false;

  @Builder.Default private boolean active = true;

  @Builder.Default private boolean deactivated = false;

  @Builder.Default @DBRef private List<Collaborators> addedUsersId = new ArrayList<>();

  @Builder.Default @DBRef private List<Block> blocks = new ArrayList<>();

  @Builder.Default private boolean deleted = false;

  @Builder.Default private boolean archived = false;

  @Builder.Default private boolean template = false;

  @Builder.Default private boolean premium = false;

  private ScheduledSurvey scheduledSurvey;

  @CreatedBy @JsonIgnore String createdBy;

  @CreatedDate @JsonIgnore Date createdDate;

  @LastModifiedDate @JsonIgnore Date lastModifiedDate;

  @LastModifiedBy @JsonIgnore String lastModifiedBy;
}
