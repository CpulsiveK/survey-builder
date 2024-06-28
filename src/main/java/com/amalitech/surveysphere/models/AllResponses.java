package com.amalitech.surveysphere.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AllResponses {
  private String question;
  private String questionType;
  private int answered;
  private int skipped;
  private String averageTime;
  private List<Response> responses;
  private List<String> options;
}
