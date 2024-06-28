package com.amalitech.surveysphere.services.responseService;

import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
import com.amalitech.surveysphere.models.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * Service implementation for providing response-related services for users.
 */
@Service
public class UserResponseServiceImplementation implements UserResponseService {

    /**
     * Sends a success response with user details.
     *
     * @param user               The user whose details will be included in the response.
     * @param httpServletRequest The HTTP request associated with the response.
     * @return A UserResponseDto representing the success response.
     */
    @Override
    public UserResponseDto sendSuccessResponse(User user, HttpServletRequest httpServletRequest) {
        return UserResponseDto.builder()
                .accessToken(httpServletRequest.getSession().getId())
                .id(user.getId())
                .username(user.getUsername())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .socialProviders(user.getSocialLogins())
                .email(user.getEmail())
                .isAccountEnabled(user.isEnabled())
                .aiCount(user.getAiCount())
                .subscriptionCode(user.getSubscriptionCode())
                .build();
    }
}
