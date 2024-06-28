package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    Optional<Token> findByUserId(String userId);

}
