package com.amalitech.surveysphere.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
public class UserTest {
    @Id
    private String id;

    private String name;

    private String username;

    private String email;

    private String password;

    private String role;

    @DBRef
    private List<SurveyTest> surveys;

    @DBRef
    private List<TokenTest> tokens;
}
