package com.amalitech.surveysphere.models;

import com.amalitech.surveysphere.enums.SocialLoginProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user")
public class User implements UserDetails {
  @Id private String id;

  private String name;

  private String username;

  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private String role;
  @Builder.Default private boolean verified = false;
  private String profilePicture;

  @Builder.Default private List<SocialLoginProvider> socialLogins = new ArrayList<>();

  @DBRef @Builder.Default private List<Survey> surveys = new ArrayList<>();

  @DBRef @Builder.Default private List<Token> tokens = new ArrayList<>();

  @DBRef private Respondent respondent;

  private String subscriptionCode;

  public void addSurveyId(Survey survey) {
    if (surveys == null) {
      surveys = new ArrayList<>();
    }
    surveys.add(survey);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role));
    return authorities;
  }

  @Builder.Default private boolean enabled = true;
  @Builder.Default private boolean deleted = true;

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return deleted;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  private int aiCount = 0;

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @CreatedBy String createdBy;

  @CreatedDate Date createdDate;

  @LastModifiedDate Date lastModifiedDate;

  @LastModifiedBy String lastModifiedBy;
}
