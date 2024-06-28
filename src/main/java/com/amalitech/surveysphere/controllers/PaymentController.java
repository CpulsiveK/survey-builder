package com.amalitech.surveysphere.controllers;

import com.amalitech.surveysphere.dto.requestDto.PaymentRequestDto;
import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
import com.amalitech.surveysphere.services.paymentService.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/survey-sphere/user/payment")
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping("/initialize")
  public ResponseEntity<Object> initializeTransaction(
      @RequestBody PaymentRequestDto paymentRequestDto,
      @RequestParam(value = "subscription_code", required = false) String subscriptionCode) {
    return new ResponseEntity<>(
        paymentService.initializePayment(paymentRequestDto, subscriptionCode), HttpStatus.OK);
  }

  @GetMapping("/verify/{reference}")
  public ResponseEntity<UserResponseDto> verifyPayment(
      @PathVariable("reference") String reference,
      @RequestParam("plan_code") String planCode,
      @RequestParam(value = "subscription_code", required = false) String subscriptionCode,
      HttpServletRequest httpServletRequest)
      throws InterruptedException, IOException {
    return new ResponseEntity<>(
        paymentService.verifyPayment(reference, planCode, subscriptionCode, httpServletRequest),
        HttpStatus.OK);
  }
}
