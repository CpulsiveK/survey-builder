package com.amalitech.surveysphere.models;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
  private int blockIndex = 0;
  private int questionIndex = 0;
  private String choice = "";
  private String question = "" ;
  private List<String> conditionalOptions = new ArrayList<>();
  private String type = "";
  private String answer = "";
}
