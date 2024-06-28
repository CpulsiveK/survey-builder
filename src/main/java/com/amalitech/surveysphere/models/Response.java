package com.amalitech.surveysphere.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "response")
public class Response {
  @Id private String id;
  private String question;

  private String questionType;

  private String questionId;

  private List<String> answer;

  private List<String> options;
}
