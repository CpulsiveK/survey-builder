package com.amalitech.surveysphere.client;

import com.amalitech.surveysphere.dto.paystackDto.DisableSubscriptionRequestDto;
import com.amalitech.surveysphere.dto.paystackDto.PaymentResponse;
import com.amalitech.surveysphere.dto.paystackDto.Subscription;
import com.amalitech.surveysphere.dto.paystackDto.SubscriptionResponse;
import com.amalitech.surveysphere.dto.requestDto.PaymentRequestDto;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface PaystackClient {
  @PostExchange("/transaction/initialize")
  Object initializePayment(
      @RequestHeader Map<String, String> headers, @RequestBody PaymentRequestDto paymentRequestDto);

  @GetExchange("/transaction/verify/{reference}")
  PaymentResponse verifyPayment(@RequestHeader Map<String, String> headers, @PathVariable String reference);

  @GetExchange("/subscription/{code}")
  Subscription fetchSubscription(
      @RequestHeader Map<String, String> headers, @PathVariable String code);

  @PostExchange("/subscription/disable")
  void disableSubscription(
      @RequestHeader Map<String, String> headers,
      @RequestBody DisableSubscriptionRequestDto disableSubscriptionRequestDto);

  @GetExchange("/subscription")
  SubscriptionResponse listSubscription(
      @RequestHeader Map<String, String> headers,
      @RequestParam("customer") Integer customer);
}
