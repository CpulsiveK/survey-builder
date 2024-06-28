package com.amalitech.surveysphere.controllers;

import com.amalitech.surveysphere.dto.requestDto.SurveyDto;
import com.amalitech.surveysphere.models.*;
import com.amalitech.surveysphere.repositories.ChatRepository;
import com.amalitech.surveysphere.repositories.UserRepository;
import com.amalitech.surveysphere.services.chatRoomService.ChatRoomService;
import com.amalitech.surveysphere.services.surveyService.SurveyService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling WebSocket messages. */
@RestController
@RequiredArgsConstructor
public class WebSocketController {
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatRepository chatRepository;
  private final SurveyService surveyService;
  private final ChatRoomService chatRoomService;
  private final UserRepository userRepository;

  /**
   * Handler for updating survey UI via WebSocket.
   *
   * @param surveyDto The DTO containing survey details
   */
  @MessageMapping("/update")
  public void updateSurveyUi(@Payload @RequestBody SurveyDto surveyDto) {
    System.out.println(surveyDto);
//    messagingTemplate.convertAndSend(
//        "/topic2/" + surveyDto.getId(), surveyService.updateSurvey(surveyDto, surveyDto.getId()));
  }

  /**
   * Handler for sending chat messages via WebSocket.
   *
   * @param chatMessage The chat message to be sent
   */
  @MessageMapping("/echo")
  public void sendMessage(@Payload ChatMessage chatMessage) {
    String timeString = new Date().toString();
    chatMessage.setTimestamp(timeString);
    if (!Objects.equals(chatMessage.getSender(), "Admin")) {
      if (chatRoomService.getFirstTimeChat(chatMessage.getSender())) {
        userRepository
            .findById(chatMessage.getSender())
            .ifPresent(
                user -> {
                  messagingTemplate.convertAndSend("/topic/newUser", user.getEmail());
                });
      }
    }
    userRepository
        .findById(chatMessage.getSender())
        .ifPresent(
            user -> {
              messagingTemplate.convertAndSend("/topic/notification", user.getEmail());
            });

    chatRepository.save(chatMessage);

    messagingTemplate.convertAndSend("/topic/" + chatMessage.getRecipient(), chatMessage);
  }

  /**
   * Handler for sending user notifications via WebSocket.
   *
   * @param userNotification The user notification to be sent
   */
  @MessageMapping("/sendNotification")
  public void sendNotification(@Payload UserNotification userNotification) {
    userNotification.setTimestamp(new Date());
    messagingTemplate.convertAndSendToUser(
        userNotification.getRecipient(), "/queue/user/notification", userNotification);
  }

  /**
   * Handler for sending public notifications via WebSocket.
   *
   * @param notificationMessage The notification message to be sent
   * @return The notification message sent to the topic
   */
  @MessageMapping("/public-notification")
  @SendTo("/topic/public/notification")
  public NotificationMessage publicNotification(@Payload NotificationMessage notificationMessage) {
    return notificationMessage;
  }
}
