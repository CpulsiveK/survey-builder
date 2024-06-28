package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.Code;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends MongoRepository<Code, String> {
    Code findByUserId(String userId);
}
