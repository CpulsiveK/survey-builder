package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.Respondent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespondentRepository extends MongoRepository<Respondent, String> {
  Optional<Respondent> findByEmail(String email);
  List<Respondent> findBySurveyId(String surveyId);

  @Query(value = "{ 'surveyId' : ?0 }", count = true)
  int countResponsesBySurveyId(String surveyId);}
