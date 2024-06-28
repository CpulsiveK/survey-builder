package com.amalitech.surveysphere.services.chatRoomService;

import com.amalitech.surveysphere.dto.requestDto.ChatRoomUserDto;
import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.models.ChatMessage;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.ChatRepository;
import com.amalitech.surveysphere.repositories.UserRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
  private final ChatRepository chatRepository;
  private final UserRepository userRepository;
  private final MongoTemplate mongoTemplate;

  @Override
  public List<String> getChatRoomUsers(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.Direction.DESC, "createdAt");

    List<ChatMessage> chats = chatRepository.findDistinctSenders(pageable);

    Set<String> senderIds = chats.stream().map(ChatMessage::getSender).collect(Collectors.toSet());

    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("_id").in(senderIds)), Aggregation.project("email"));

    List<String> userEmails =
        mongoTemplate.aggregate(aggregation, "user", String.class).getMappedResults().stream()
            .map(
                json -> {
                  try {
                    JSONObject jsonObject = new JSONObject(json);
                    return jsonObject.getString("email");
                  } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    return userEmails;
  }

  @Override
  public List<ChatMessage> getChatMessages(ChatRoomUserDto chatRoomUserDto) {
    Optional<User> user = userRepository.findByEmail(chatRoomUserDto.getEmail());
    if (user.isEmpty()) {
      throw new NotFoundException("Admin not found");
    }

    return chatRepository.findBySenderAndRecipientOrSenderAndRecipient(
        user.get().getId(),
        chatRoomUserDto.getAdminId(),
        chatRoomUserDto.getAdminId(),
        user.get().getId());
  }

  @Override
  public Boolean getFirstTimeChat(String senderId) {

    return chatRepository
        .findBySenderAndRecipientOrSenderAndRecipient(senderId, "Admin", "Admin", senderId)
        .isEmpty();
  }
}
