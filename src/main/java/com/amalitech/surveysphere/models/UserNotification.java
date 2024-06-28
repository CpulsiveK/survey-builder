package com.amalitech.surveysphere.models;

import jdk.jfr.Timestamp;
import lombok.Data;

import java.util.Date;

@Data
public class UserNotification {
  @Timestamp 
  private Date timestamp;
  private String content;
  private String recipient;
}
