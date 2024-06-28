package com.amalitech.surveysphere.services.tokenService;

import io.jsonwebtoken.Claims;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;

/** Interface defining operations related to token management. */
public interface TokenService {
  /**
   * Generates a JWT token for the provided UserDetails and userId.
   *
   * @param userDetails The UserDetails object containing user details.
   * @param userId The ID of the user.
   * @return A JWT token as a String.
   */
  String generateToken(UserDetails userDetails, String userId);

  /**
   * Generates a JWT token with extra claims for the provided UserDetails and userId.
   *
   * @param extraClaims Additional claims to include in the token.
   * @param userDetails The UserDetails object containing user details.
   * @param userId The ID of the user.
   * @return A JWT token as a String.
   */
  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String userId);

  /**
   * Extracts the username from the provided JWT token.
   *
   * @param token The JWT token.
   * @return The username extracted from the token.
   */
  String extractUsername(String token);

  /**
   * Extracts a specific claim from the provided JWT token.
   *
   * @param <T> The type of the claim.
   * @param token The JWT token.
   * @param claimResolver The function to resolve the claim.
   * @return The resolved claim.
   */
  <T> T extractClaim(String token, Function<Claims, T> claimResolver);

  /**
   * Checks if the provided JWT token is valid for the given UserDetails.
   *
   * @param token The JWT token.
   * @param userDetails The UserDetails object containing user details.
   * @return True if the token is valid, false otherwise.
   */
  boolean isTokenValid(String token, UserDetails userDetails);

  /**
   * Checks if the provided JWT token is expired.
   *
   * @param token The JWT token.
   * @return True if the token is expired, false otherwise.
   */
  boolean isTokenExpired(String token);

  /**
   * Extracts the expiration date from the provided JWT token.
   *
   * @param token The JWT token.
   * @return The expiration date extracted from the token.
   */
  Date extractExpiration(String token);

  /**
   * Extracts all claims from the provided JWT token.
   *
   * @param token The JWT token.
   * @return All claims extracted from the token.
   */
  Claims extractAllClaims(String token);

  /**
   * Retrieves the signing key used for JWT token verification.
   *
   * @return The signing key as a Key object.
   */
  Key getSignInKey();
}
