package com.amalitech.surveysphere.dto.responseDto;

import com.amalitech.surveysphere.models.AllResponses;
import com.amalitech.surveysphere.models.Respondent;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseAnalysisDto {
  private int responseCount;
  private boolean active;
  private List<AllResponses> responses;
  private List<Respondent> individualResults;
  private String averageTime;
}
