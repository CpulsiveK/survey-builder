package com.amalitech.surveysphere.dto.responseDto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDto {
  private String path;
  private String message;
  private final OffsetDateTime timeStamp = OffsetDateTime.now();
}
