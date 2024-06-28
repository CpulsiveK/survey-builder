package com.amalitech.surveysphere.services.otherServices.userDetailsService;

import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.UserRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.*;

/** Implementation of the custom user details service. */
@Service
@Primary
@RequiredArgsConstructor
public class UserServiceDetailsImplementation implements IUserDetailsService {
  private final UserRepository userRepository;

  /**
   * Load user details by username (email) from the database.
   *
   * @param email the email (username) of the user
   * @return UserDetails object representing the user
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Optional<User> findUser = userRepository.findByEmail(email.trim().toLowerCase());

    if (findUser.isEmpty())
      throw new UsernameNotFoundException(USER_NOT_FOUND.getMessage());

    User user = findUser.get();

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        user.isEnabled(),
        true,
        true,
        user.isDeleted(),
        getAuthorities(user));
  }

  /**
   * Retrieve user authorities (roles).
   *
   * @param user the user
   * @return a collection of GrantedAuthority objects
   */
  private Collection<GrantedAuthority> getAuthorities(User user) {
    return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
  }
}
