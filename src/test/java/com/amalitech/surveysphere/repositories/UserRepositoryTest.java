package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.UserTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryTest extends MongoRepository<UserTest,String> {

}
