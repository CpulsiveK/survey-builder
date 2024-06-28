package com.amalitech.surveysphere.services.auditService;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditService implements AuditorAware<String> {
  /**
   * Returns the current auditor of the application.
   *
   * @return the current auditor.
   */
  @Override
  public @NotNull Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return Optional.ofNullable(authentication.getName());
    } else {
      // If there is no authenticated user, return an empty Optional
      return Optional.empty();
    }
  }
}
