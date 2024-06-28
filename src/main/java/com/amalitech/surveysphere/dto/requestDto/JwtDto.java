package com.amalitech.surveysphere.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtDto {
  @NotBlank(message = "credential cannot be blank")
  String credential;
}
