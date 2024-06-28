package com.amalitech.surveysphere.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTest {
    @Id
    private String id;

    private String userId;

    private String anonymousUserId;

    private String responses;

    private String questionId;

    private SurveyTest survey;
}
