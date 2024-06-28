package com.amalitech.surveysphere.services.chatRoomService;

import com.amalitech.surveysphere.dto.requestDto.ChatRoomUserDto;
import com.amalitech.surveysphere.models.ChatMessage;

import java.util.List;

public interface ChatRoomService {
  List<String> getChatRoomUsers(int page, int limit);

  List<ChatMessage> getChatMessages(ChatRoomUserDto chatRoomUserDto);

  Boolean getFirstTimeChat(String senderId);
}
