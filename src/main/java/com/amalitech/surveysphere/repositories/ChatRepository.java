package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.ChatMessage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<ChatMessage, String> {
  @Query(value = "{ sender: { $ne: 'Admin' } }", fields = "{ 'sender': 1 }")
  List<ChatMessage> findDistinctSenders(Pageable pageable);

  List<ChatMessage> findBySenderAndRecipientOrSenderAndRecipient(
      String sender, String recipient, String recipient2, String sender2);
}
