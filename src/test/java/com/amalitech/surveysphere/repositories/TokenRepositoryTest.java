package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.TokenTest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepositoryTest extends MongoRepository<TokenTest,String> {
}
