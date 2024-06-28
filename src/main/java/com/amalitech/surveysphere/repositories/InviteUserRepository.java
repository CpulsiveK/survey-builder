package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.Collaborators;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteUserRepository extends MongoRepository<Collaborators,String> {
    Optional<Collaborators> findByHashedUserIdAndSurveyId(String userId, String surveyId);
    Optional<Collaborators> findByUserIdAndSurveyId(String userId, String surveyId);
}
