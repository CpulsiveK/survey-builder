package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.Response;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends MongoRepository<Response, String> {
    List<Response> findAllByQuestionId(String questionId);
}
