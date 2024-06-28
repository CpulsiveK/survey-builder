package com.amalitech.surveysphere.dto.requestDto;

import com.amalitech.surveysphere.models.Style;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadDto {
//  private Map<String, String> style;
  private Style style;

  @NotBlank(message = "url cannot be blank")
  private String url = "https://survey-sphere-bucket.s3.amazonaws.com/$2a$10$DBIEZ2/2L7QL0d9my/yxjuSRLr0NL2M4Ya.Yg5KyJr1JGWMM4KW1i" ;
}
