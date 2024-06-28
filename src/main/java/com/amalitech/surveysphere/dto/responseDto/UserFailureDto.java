package com.amalitech.surveysphere.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFailureDto {
  String message;
  String path;
}
