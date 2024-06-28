package com.amalitech.surveysphere.services.paymentService;

import com.amalitech.surveysphere.dto.requestDto.PaymentRequestDto;
import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface PaymentService {
    Object initializePayment(PaymentRequestDto paymentRequestDto, String subscriptionCode);
    UserResponseDto verifyPayment(String reference, String planCode,
                                  String subscriptionCode, HttpServletRequest httpServletRequest) throws InterruptedException
            , IOException;
}
