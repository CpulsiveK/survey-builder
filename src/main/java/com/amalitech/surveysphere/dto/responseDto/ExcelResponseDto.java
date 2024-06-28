package com.amalitech.surveysphere.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelResponseDto {
  byte[] url;
  String filename;
}
