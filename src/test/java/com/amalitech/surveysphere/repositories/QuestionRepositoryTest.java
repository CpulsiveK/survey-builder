package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.QuestionTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepositoryTest extends MongoRepository<QuestionTest,String> {
}
