package com.amalitech.surveysphere.models;

import lombok.Data;

@Data
public class SurveyUpdateMessage {
    String surveyId;
    Object updateContent;
    String updatedBy;
    String updateTime;
}
