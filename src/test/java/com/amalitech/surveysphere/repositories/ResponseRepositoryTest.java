package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.ResponseTest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResponseRepositoryTest extends MongoRepository<ResponseTest,String> {
}
