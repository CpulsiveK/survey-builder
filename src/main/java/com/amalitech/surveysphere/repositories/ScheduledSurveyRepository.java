package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.ScheduledSurvey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledSurveyRepository extends MongoRepository<ScheduledSurvey, String> {
  Optional<ScheduledSurvey> findBySurveyId(String surveyId);

    List<ScheduledSurvey> findAllByActionAndIsCompletedAndScheduledDateBefore(String action, boolean isCompleted,
                                                                              LocalDateTime localDateTime);
}
