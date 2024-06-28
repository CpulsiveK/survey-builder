package com.amalitech.surveysphere.dto.requestDto;

import com.amalitech.surveysphere.models.Block;
import com.amalitech.surveysphere.models.ColorScheme;
import com.amalitech.surveysphere.models.SurveyTitle;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyDto {

  private String id;

  @NotNull(message = "logo must not be null")
  private FileUploadDto logo;


  @NotNull(message = "surveyTitle must not be null")
  private SurveyTitle surveyTitle;

  @NotNull(message = "blocks must not be null")
  @NotEmpty(message = "blocks must not be empty")
  private List<Block> blocks;

  @NotNull(message = "colorScheme must not be null")
  private ColorScheme colorScheme;

  private String category;

  @NotNull(message = "surveyView must not be null")
  @NotBlank(message = "surveyView must not be blank")
  private String surveyView;

  private boolean premium;
}
