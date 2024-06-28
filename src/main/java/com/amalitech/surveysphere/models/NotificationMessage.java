package com.amalitech.surveysphere.models;

import java.util.Date;
import jdk.jfr.Timestamp;
import lombok.Data;

@Data
public class NotificationMessage {
  @Timestamp
  private Date timestamp;
  private String content;
}
