package com.amalitech.surveysphere.dto.paystackDto;

import lombok.Data;

@Data
public class SubscriptionData {
    private Long integration;
    private String domain;
    private String start;
    private String status;
    private int quantity;
    private Double amount;
    private Authorization authorization;
    private String subscription_code;
    private String email_token;
    private Long id;
    private String createdAt;
    private String updatedAt;
}
