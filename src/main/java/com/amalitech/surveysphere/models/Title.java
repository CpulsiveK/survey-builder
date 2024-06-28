package com.amalitech.surveysphere.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Title {
    private String titleName;
    private SettingsStyle style;
}
