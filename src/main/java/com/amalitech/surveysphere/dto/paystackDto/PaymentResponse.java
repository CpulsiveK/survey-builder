package com.amalitech.surveysphere.dto.paystackDto;

import lombok.Data;

@Data
public class PaymentResponse {
  private boolean status;
  private String message;
  private com.amalitech.surveysphere.dto.paystackDto.Data data;
}
