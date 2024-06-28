package com.amalitech.surveysphere.enums;

import lombok.Getter;

/** Enum representing error messages */
@Getter
public enum CustomExceptionMessage {
    SURVEY_NOT_FOUND("Survey does not exist"),
    COLLABORATOR_NOT_FOUND("Collaborator not found"),
    USER_ALREADY_REGISTERED("User already registered"),
    USER_NOT_FOUND("User not found"),
    USER_NOT_AUTHENTICATED("User not authenticated"),
    SAME_PASSWORD("New password is the same as the old password"),
    INVALID_ACTIVATION_TYPE("Invalid activation type"),
    USER_ALREADY_AN_ADMIN("User is already an admin"),
    QUESTION_NOT_FOUND("Question not found"),
    PAYMENT_FAILED("Payment verification failed, please try again!");

    final String message;

    CustomExceptionMessage(String message) {
        this.message = message;
    }
}
