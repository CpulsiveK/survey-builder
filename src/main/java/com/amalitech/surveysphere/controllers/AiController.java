package com.amalitech.surveysphere.controllers;

import com.amalitech.surveysphere.services.aiservice.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/survey-sphere/user")
@RequiredArgsConstructor
public class AiController {
  private final AiService aiService;

  @GetMapping("/create-survey-ai")
  public Mono<Object> createSurveysAI(@AuthenticationPrincipal UserDetails userDetails,
      @RequestParam String title,
      @RequestParam String category,
      @RequestParam String description,
      @RequestParam String blocks,
      @RequestParam String targetAudience,
      @RequestParam String questions) {
    return aiService.createSurveysAI(
        title, category, description, blocks, targetAudience, questions,userDetails);
  }
}
