package com.amalitech.surveysphere.dto.paystackDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionRequestDto {
    private String customer;
    private String plan;
    private String authorization;
    private String start_date;
}
