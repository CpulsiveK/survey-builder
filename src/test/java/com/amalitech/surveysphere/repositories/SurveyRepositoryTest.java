package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.UserTest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SurveyRepositoryTest extends MongoRepository<UserTest,String> {
}
