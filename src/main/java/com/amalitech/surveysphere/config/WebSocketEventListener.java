package com.amalitech.surveysphere.config;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

/** Listener for handling session connect events. */
@Component
public class WebSocketEventListener implements ApplicationListener<SessionConnectEvent> {

  /**
   * Handles the session connect event.
   *
   * @param event The SessionConnectEvent object representing the session connect event
   */
  @Override
  public void onApplicationEvent(SessionConnectEvent event) {
    long user = event.getTimestamp();
    System.out.println("connected");
  }
}
