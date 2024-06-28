package com.amalitech.surveysphere.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "question")
public class Question {
  @Id
  private String id;

  private boolean required;

  private QuestionTitle title;

  private String type;

  private int answered;

  private int skipped;

  @Builder.Default private List<String> options = new ArrayList<>();

  private Condition conditions;
}
