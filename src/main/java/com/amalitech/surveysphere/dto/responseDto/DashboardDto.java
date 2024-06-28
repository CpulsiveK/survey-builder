package com.amalitech.surveysphere.dto.responseDto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDto {
  long totalSurveys;
  long totalUsers;
  long deactivatedUsers;
  long totalTemplates;
  double userGrowth;
  double surveyGrowth;
  double templateGrowth;
  double deactivatedUsersGrowth;
  private Map<String, Map<String, Long>> monthlySurveyCounts;
  private Map<String, Integer> userCountByRole;
  private Map<String, Integer> totalTemplatesCount;
}
