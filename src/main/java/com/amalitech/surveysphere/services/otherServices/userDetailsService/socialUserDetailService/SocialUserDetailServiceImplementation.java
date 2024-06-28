package com.amalitech.surveysphere.services.otherServices.userDetailsService.socialUserDetailService;

import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.*;

/** Implementation of the SocialUserDetailService interface. */
@Service
@RequiredArgsConstructor
public class SocialUserDetailServiceImplementation implements SocialUserDetailService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> userOptional = userRepository.findByEmail(email);

    return userOptional
        .map(
            user ->
                org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles("USER")
                    .build())
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.getMessage()));
  }
}
