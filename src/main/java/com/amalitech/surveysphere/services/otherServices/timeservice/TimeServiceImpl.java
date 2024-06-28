package com.amalitech.surveysphere.services.otherServices.timeservice;

import com.amalitech.surveysphere.models.Respondent;
import com.amalitech.surveysphere.repositories.RespondentRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TimeServiceImpl implements TimeService {
  private final RespondentRepository respondentRepository;

  @Override
  public String calculateAverageTimeBySurveyId(String surveyId) {
    List<Respondent> respondents = respondentRepository.findBySurveyId(surveyId);

    List<Date> times = new ArrayList<>();
    for (Respondent respondent : respondents) {
      times.add(respondent.getCreatedDate());
    }

    return calculateAverageTime(times);
  }

  private String calculateAverageTime(List<Date> times) {
    if (times.isEmpty()) {
      return "";
    }

    List<LocalDateTime> dateTimes = new ArrayList<>();
    for (Date time : times) {
      dateTimes.add(LocalDateTime.ofInstant(time.toInstant(), ZoneId.of("UTC")));
    }

    LocalDateTime minTime = dateTimes.get(0);
    LocalDateTime maxTime = dateTimes.get(0);
    Duration totalDuration = Duration.ZERO;

    for (LocalDateTime dateTime : dateTimes) {
      if (dateTime.isBefore(minTime)) {
        minTime = dateTime;
      }
      if (dateTime.isAfter(maxTime)) {
        maxTime = dateTime;
      }
      totalDuration = totalDuration.plus(Duration.between(minTime, dateTime));
    }

    long averageSeconds = totalDuration.getSeconds() / dateTimes.size();
    LocalDateTime averageDateTime = minTime.plusSeconds(averageSeconds);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return averageDateTime.format(formatter);
  }
}
