package com.amalitech.surveysphere.dto.paystackDto;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionResponse {
    private boolean status;
    private String message;
    private List<com.amalitech.surveysphere.dto.paystackDto.Data> data;
}
