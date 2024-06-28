package com.amalitech.surveysphere.controllers;

import com.amalitech.surveysphere.dto.requestDto.ChatRoomUserDto;
import com.amalitech.surveysphere.models.ChatMessage;
import com.amalitech.surveysphere.services.chatRoomService.ChatRoomService;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Controller class for managing chat-related operations. */
@RestController
@RequestMapping("/survey-sphere/user/chats")
@RequiredArgsConstructor
@Validated
public class ChatController {
  private final ChatRoomService chatRoomService;

  /**
   * Retrieves a list of chat room users.
   *
   * @param page The page number for pagination
   * @param limit The maximum number of users per page
   * @return A list of chat room users
   */
  @GetMapping
  public List<String> getChatRoomUsers(
      @RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) {
    return chatRoomService.getChatRoomUsers(page, limit);
  }

  /**
   * Retrieves chat messages for a specific user in a chat room.
   *
   * @param chatRoomUserDto DTO containing chat room user details
   * @return A list of chat messages
   */
  @PostMapping("/messages")
  public List<ChatMessage> getChatRoomUsers(@Valid @RequestBody ChatRoomUserDto chatRoomUserDto) {
    return chatRoomService.getChatMessages(chatRoomUserDto);
  }
}
