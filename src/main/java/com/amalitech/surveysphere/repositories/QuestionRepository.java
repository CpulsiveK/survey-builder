package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {}
