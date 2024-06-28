package com.amalitech.surveysphere.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyTest {


    @Id
    private String id;

    private String title;

    private String category;

    private String surveyOwner;

    private List<String> addedUsersId;

    @DBRef
    private List<QuestionTest> questions;


}
