package com.amalitech.surveysphere.enums;

import lombok.Getter;

@Getter
public enum Endpoint {
  BACKEND_URL("https://survey-sphere-api.amalitech-dev.net/survey-sphere/public/invite-admin/"),
  PAYSTACK_BASE_URL("https://api.paystack.co"),
  AI_API_URL("https://ai-api.amalitech-dev.net/api/v1/chat");

  final String url;

  Endpoint(String url) {
    this.url = url;
  }
}
