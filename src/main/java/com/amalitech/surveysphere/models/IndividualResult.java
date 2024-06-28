package com.amalitech.surveysphere.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndividualResult {
    private String email;

    private boolean isAnonymous;

    private String surveyId;
}
