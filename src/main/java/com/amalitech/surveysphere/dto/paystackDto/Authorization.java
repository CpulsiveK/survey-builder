package com.amalitech.surveysphere.dto.paystackDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Authorization {
    private String authorization_code;

    private String bin;
    private String last4;
    private String expMonth;
    private String expYear;
    private String channel;
    private String cardType;
    private String bank;
    private String countryCode;
    private String brand;
    private boolean reusable;
    private String signature;
    private String accountName;
}
