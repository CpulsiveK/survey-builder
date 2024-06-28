package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.Survey;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Repository;

@Repository
@RedisHash
public interface SurveyRepository extends MongoRepository<Survey, String> {

  Page<Survey> findAllBySurveyOwnerAndDeactivated(
      String surveyOwner, boolean deactivated, Pageable pageable);

  Page<Survey> findAllBySurveyOwnerAndSentAndArchivedAndDeleted(
      String surveyOwner, boolean sent, boolean archived, boolean deleted, Pageable pageable);

  Page<Survey> findAllBySentAndSurveyOwnerAndDeleted(
      boolean sent, String surveyOwner, boolean deleted, Pageable pageable);

  Page<Survey> findAllByArchivedAndSurveyOwnerAndDeleted(
      boolean archived, String surveyOwner, boolean deleted, Pageable pageable);

  Page<Survey> findAllByTemplateAndDeletedAndDeactivated(
      boolean template, boolean deleted, boolean deactivated, Pageable pageable);

  Page<Survey> findAllByDeactivatedAndTemplateAndDeleted(
      boolean deactivated, boolean template, boolean deleted, Pageable pageable);

  long countSurveysByTemplate(boolean template);

  long countByTemplateAndCreatedDateBetween(boolean template, Date start, Date end);

  @Query("{'createdDate': {'$gte': ?0, '$lt': ?1}}")
  List<Survey> findCreatedSurveysBetweenDates(Date start, Date end);

  @Query("{'deleted': true, 'lastModifiedDate': {'$gte': ?0, '$lt': ?1}}")
  List<Survey> findDeletedSurveysBetweenDates(Date start, Date end);

  @Query("{'archived': true, 'lastModifiedDate': {'$gte': ?0, '$lt': ?1}}")
  List<Survey> findArchivedSurveysBetweenDates(Date start, Date end);

  @Query("{'deactivated': true, 'lastModifiedDate': {'$gte': ?0, '$lt': ?1}}")
  List<Survey> findDeactivatedSurveysBetweenDates(Date start, Date end);

  Integer countByPremiumAndTemplate(boolean premium, boolean template);
}
