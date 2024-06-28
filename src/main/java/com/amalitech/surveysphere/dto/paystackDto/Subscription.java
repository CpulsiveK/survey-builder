package com.amalitech.surveysphere.dto.paystackDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    private String id;
    private String plan;
    private Authorization authorization;
    private String subscription_code;
    private String email_token;
    private String customer;
    private String status;
    private Double amount;
    private String createdAt;
    private String updatedAt;
}
