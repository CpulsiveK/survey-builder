package com.amalitech.surveysphere.services.dashboardService;

import com.amalitech.surveysphere.dto.responseDto.DashboardDto;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.SurveyRepository;
import com.amalitech.surveysphere.repositories.UserRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
  private final UserRepository userRepository;
  private final SurveyRepository surveyRepository;

  @Override
  public DashboardDto getDashboardData() {
    return DashboardDto.builder()
        .totalSurveys(surveyRepository.count())
        .totalUsers(userRepository.count())
        .totalTemplates(surveyRepository.countSurveysByTemplate(true))
        .deactivatedUsers(userRepository.countUserByEnabled(false))
        .userGrowth(calculatePercentageOfUsersAddedWithinAWeek())
        .surveyGrowth(calculatePercentageOfTemplatesCreatedWithinAWeek())
        .templateGrowth(calculatePercentageOfSurveysCreatedWithinAWeek())
        .deactivatedUsersGrowth(calculatePercentageOfEnabledUsersAddedWithinAWeek())
        .monthlySurveyCounts(getSurveyCountsByMonth())
        .totalTemplatesCount(getTotalTemplateCounts())
        .userCountByRole(getUserCountsByRole())
        .build();
  }

  private long calculatePercentageOfUsersAddedWithinAWeek() {
    Date currentDate = new Date();
    Date oneWeekAgo = new Date(currentDate.getTime() - 7 * 24 * 60 * 60 * 1000);

    List<User> usersAddedWithinAWeek =
        userRepository.findByCreatedDateBetween(oneWeekAgo, currentDate);
    long totalUsers = userRepository.count();
    long usersAddedWithinAWeekCount = usersAddedWithinAWeek.size();

    return usersAddedWithinAWeekCount / totalUsers * 100;
  }

  private double calculatePercentageOfEnabledUsersAddedWithinAWeek() {
    Date currentDate = new Date();
    Date oneWeekAgo = new Date(currentDate.getTime() - 7 * 24 * 60 * 60 * 1000);

    List<User> enabledUsersAddedWithinAWeek =
        userRepository.findByEnabledAndCreatedDateBetween(true, oneWeekAgo, currentDate);
    long totalEnabledUsers = userRepository.countByEnabled(true);
    long enabledUsersAddedWithinAWeekCount = enabledUsersAddedWithinAWeek.size();

    return (double) enabledUsersAddedWithinAWeekCount / totalEnabledUsers * 100;
  }

  private double calculatePercentageOfTemplatesCreatedWithinAWeek() {
    Date currentDate = new Date();
    Date oneWeekAgo = new Date(currentDate.getTime() - 7 * 24 * 60 * 60 * 1000);

    long totalTemplates = surveyRepository.countSurveysByTemplate(true);
    long templatesCreatedWithinAWeek =
        surveyRepository.countByTemplateAndCreatedDateBetween(true, oneWeekAgo, currentDate);

    if (totalTemplates == 0) {
      return 0; // Avoid division by zero
    }

    return ((double) templatesCreatedWithinAWeek / totalTemplates) * 100;
  }

  private double calculatePercentageOfSurveysCreatedWithinAWeek() {
    Date currentDate = new Date();
    Date oneWeekAgo = new Date(currentDate.getTime() - 7 * 24 * 60 * 60 * 1000);

    long totalSurveys = surveyRepository.count();
    long surveysCreatedWithinAWeek =
        surveyRepository.countByTemplateAndCreatedDateBetween(false, oneWeekAgo, currentDate);

    if (totalSurveys == 0) {
      return 0; // Avoid division by zero
    }

    return ((double) surveysCreatedWithinAWeek / totalSurveys) * 100;
  }

  public Map<String, Map<String, Long>> getSurveyCountsByMonth() {
    Map<String, Map<String, Long>> result = new LinkedHashMap<>();
    Calendar calendar = Calendar.getInstance();

    for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
      calendar.set(Calendar.MONTH, month);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      Date start = getStartOfMonth(calendar);
      Date end = getEndOfMonth(calendar);

      String monthName =
          calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
      result.put(monthName, getCountsForMonth(start, end));
    }

    return result;
  }

  private Map<String, Long> getCountsForMonth(Date start, Date end) {
    Map<String, Long> counts = new HashMap<>();

    counts.put(
        "created", (long) surveyRepository.findCreatedSurveysBetweenDates(start, end).size());
    counts.put(
        "deleted", (long) surveyRepository.findDeletedSurveysBetweenDates(start, end).size());
    counts.put(
        "archived", (long) surveyRepository.findArchivedSurveysBetweenDates(start, end).size());
    counts.put(
        "deactivated",
        (long) surveyRepository.findDeactivatedSurveysBetweenDates(start, end).size());

    return counts;
  }

  private Date getStartOfMonth(Calendar calendar) {
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  private Date getEndOfMonth(Calendar calendar) {
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }

  private Map<String, Integer> getUserCountsByRole() {
    Map<String, Integer> userCountsByRole = new HashMap<>();
    userCountsByRole.put("USER", userRepository.countByRole("USER"));
    userCountsByRole.put("TEAM", userRepository.countByRole("TEAM"));
    userCountsByRole.put("BUSINESS", userRepository.countByRole("BUSINESS"));
    return userCountsByRole;
  }

  public Map<String, Integer> getTotalTemplateCounts() {
    Map<String, Integer> templateCounts = new HashMap<>();
    templateCounts.put("premium", surveyRepository.countByPremiumAndTemplate(true, true));
    templateCounts.put("free", surveyRepository.countByPremiumAndTemplate(false, true));
    return templateCounts;
  }
}
