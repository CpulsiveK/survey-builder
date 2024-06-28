package com.amalitech.surveysphere.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "survey-style")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingsStyle {

  boolean bold = false;

  boolean italic= false;
  boolean underline = false;

  boolean strikethrough = false;
  boolean small= false;
}
