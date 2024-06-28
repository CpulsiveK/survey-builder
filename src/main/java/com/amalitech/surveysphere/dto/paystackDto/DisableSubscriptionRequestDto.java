package com.amalitech.surveysphere.dto.paystackDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DisableSubscriptionRequestDto {
    private String code;
    private String token;
}
