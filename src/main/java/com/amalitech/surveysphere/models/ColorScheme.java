package com.amalitech.surveysphere.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorScheme {
    private String textColor = "black" ;
    private String backgroundColor = "#F7F9FC";
}
