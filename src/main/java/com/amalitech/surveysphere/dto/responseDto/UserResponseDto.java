package com.amalitech.surveysphere.dto.responseDto;

import com.amalitech.surveysphere.enums.SocialLoginProvider;
import com.amalitech.surveysphere.models.Respondent;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
  private String id;
  private String accessToken;
  private String username;
  private String email;
  private String profilePicture;
  private boolean verified;
  private List<SocialLoginProvider> socialProviders;
  private String role;
  private Respondent respondent;
  private boolean isAccountEnabled;
  private int aiCount;
  private String subscriptionCode;
}
