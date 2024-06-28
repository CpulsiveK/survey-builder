package com.amalitech.surveysphere.dto.responseDto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AiSurveyDto implements Serializable {
  private String title;
  private String description;
  private String category;
  private List<AiBlock> blocks;
}
