package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountManagementDto {
  @NotBlank(message = "ID cannot be blank")
  private String id;

  @NotBlank(message = "Status cannot be blank")
  private String status;
}
