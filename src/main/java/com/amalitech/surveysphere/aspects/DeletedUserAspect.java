package com.amalitech.surveysphere.aspects;

import com.amalitech.surveysphere.exceptions.DeactivatedException;
import com.amalitech.surveysphere.exceptions.DeletedException;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class DeletedUserAspect {
  private final UserRepository userRepository;

  @Before("execution(* com.amalitech.surveysphere.services.*.*.*(..))")
  public void checkUserDeletion() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      String userEmail = authentication.getName(); // Assuming email is used as username

      Optional<User> user = userRepository.findByEmail(userEmail);
      if (user.isPresent()) {
        User userExist = user.get();
        if (!userExist.isDeleted()) {
          throw new DeletedException(
              "User with account " + userExist.getEmail() + " has been deleted!!!");
        }
        if (!userExist.isEnabled()) {
          throw new DeactivatedException("Your account has been deactivated!!!");
        }
      }
    }
  }
}
