package com.amalitech.surveysphere.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chat")
public class ChatMessage {
  @Id private String id;
  @Timestamp private String timestamp;
  private String content;
  private String sender;
  private String recipient;

  @JsonIgnore
  @CreatedBy
  String createdBy;

  @JsonIgnore
  @CreatedDate
  Date createdDate;

  @JsonIgnore
  @LastModifiedDate
  Date lastModifiedDate;

  @JsonIgnore
  @LastModifiedBy
  String lastModifiedBy;
}
