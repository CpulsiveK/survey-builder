package com.amalitech.surveysphere.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveySettingDto {
  private FileUploadDto file;
  private String userId;
  private String surveyId;
  private String pageColor;
  private String fontColor;
  private String inputBackground;
  private String fontName;
  private Integer fontSize;
  private Integer formWidth;
  private String labelAlignment;
  private Integer questionSpacing;
  private Integer labelWidth;
}
