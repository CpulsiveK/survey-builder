package com.amalitech.surveysphere.services.aiservice;

import com.amalitech.surveysphere.dto.responseDto.AiSurveyDto;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface AiService {
  Mono<Object> createSurveysAI(
          String title,
          String category,
          String description,
          String blocks,
          String targetAudience,
          String questions, UserDetails userDetails);
}
