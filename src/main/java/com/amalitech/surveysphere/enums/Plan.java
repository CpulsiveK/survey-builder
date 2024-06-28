package com.amalitech.surveysphere.enums;

import lombok.Getter;

@Getter
public enum Plan {
  TEAM("PLN_ry32mjtuw4n8ksj"),
  BUSINESS("PLN_5cjploncunjjsf4");

  final String plan;

  Plan(String plan) {
    this.plan = plan;
  }
}
