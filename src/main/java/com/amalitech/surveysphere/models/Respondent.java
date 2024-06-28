package com.amalitech.surveysphere.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "respondent")
public class Respondent {
  @Id private String id;

  private String email;

  private boolean isAnonymous;

  private String surveyId;

  @Builder.Default @DBRef private List<Response> responses = new ArrayList<>();

  @JsonFormat(pattern = "HH:mm", timezone = "UTC")
  @CreatedDate
  Date createdDate;

}
