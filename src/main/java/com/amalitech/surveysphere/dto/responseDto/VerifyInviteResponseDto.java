package com.amalitech.surveysphere.dto.responseDto;

import com.amalitech.surveysphere.models.Collaborators;
import lombok.Data;

@Data
public class VerifyInviteResponseDto {
    boolean verified;
    Collaborators collaborator;
}
