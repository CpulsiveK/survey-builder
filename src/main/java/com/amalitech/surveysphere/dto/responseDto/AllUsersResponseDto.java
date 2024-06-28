package com.amalitech.surveysphere.dto.responseDto;

import com.amalitech.surveysphere.models.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AllUsersResponseDto {
    private List<User> users;
    private int totalPages;
}
