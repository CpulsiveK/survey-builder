package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.Block;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRespository extends MongoRepository<Block, String> {}
