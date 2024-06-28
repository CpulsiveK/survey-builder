package com.amalitech.surveysphere.enums;

import java.util.Arrays;
import java.util.Optional;

/** Enum representing social login providers. */
public enum SocialLoginProvider {
  GOOGLE,
  GITHUB,
  FACEBOOK;

  /**
   * Finds a SocialLoginProvider by its name (case-insensitive).
   *
   * @param providerName The name of the provider to find.
   * @return An Optional containing the found SocialLoginProvider, or empty if not found.
   */
  public static Optional<SocialLoginProvider> findByProviderName(String providerName) {
    return Arrays.stream(values())
        .filter(provider -> provider.name().equalsIgnoreCase(providerName))
        .findFirst();
  }
}
