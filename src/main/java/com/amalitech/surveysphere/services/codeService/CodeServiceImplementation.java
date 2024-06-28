package com.amalitech.surveysphere.services.codeService;

import com.amalitech.surveysphere.models.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

/** Implementation class for generating codes. */
@Service
@RequiredArgsConstructor
public class CodeServiceImplementation implements CodeService {
  private final PasswordEncoder passwordEncoder;
  private final MongoTemplate mongoTemplate;

  /**
   * Generates a code for the specified user ID.
   *
   * @param userId The ID of the user for whom the code will be generated.
   * @return The generated code as a String.
   */
  @Override
  public String generateCode(String userId) {
    int count = 5;
    StringBuilder verificationCode = new StringBuilder();

    Random random = new Random();

    for (int i = 0; i < count; i++) {
      verificationCode.append(random.nextInt(10));
      String hashedCode = passwordEncoder.encode(verificationCode);

      Query query = Query.query(Criteria.where("userId").is(userId));
      Update update = new Update().set("code", hashedCode);

      mongoTemplate.upsert(query, update, Code.class);
    }
    return verificationCode.toString();
  }
}
