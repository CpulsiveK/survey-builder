package com.amalitech.surveysphere.services.tokenService;

import com.amalitech.surveysphere.models.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Service implementation class for token management. */
@Service
@RequiredArgsConstructor
public class TokenServiceImplementation implements TokenService {
  private final Environment env;
  private final PasswordEncoder passwordEncoder;
  private final MongoTemplate mongoTemplate;

  /**
   * Generates a JWT token for the provided UserDetails and userId.
   *
   * @param userDetails The UserDetails object containing user details.
   * @param userId The ID of the user.
   * @return A JWT token as a String.
   */
  @Override
  public String generateToken(UserDetails userDetails, String userId) {

    return generateToken(new HashMap<>(), userDetails, userId);
  }

  /**
   * Generates a JWT token with extra claims for the provided UserDetails and userId.
   *
   * @param extraClaims Additional claims to include in the token.
   * @param userDetails The UserDetails object containing user details.
   * @param userId The ID of the user.
   * @return A JWT token as a String.
   */
  @Override
  public String generateToken(
      Map<String, Object> extraClaims, UserDetails userDetails, String userId) {
    String jwtToken =
        Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();

    var hashedToken = passwordEncoder.encode(jwtToken);

    Query query = Query.query(Criteria.where("userId").is(userId));
    Update update =
        new Update()
            .set("token", hashedToken)
            .set("expirationDate", isTokenExpired(jwtToken))
            .set("isTokenExpired", isTokenExpired(jwtToken));

    mongoTemplate.upsert(query, update, Token.class);
    return jwtToken;
  }

  /**
   * Extracts the username from the provided JWT token.
   *
   * @param token The JWT token.
   * @return The username extracted from the token.
   */
  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a specific claim from the provided JWT token.
   *
   * @param <T> The type of the claim.
   * @param token The JWT token.
   * @param claimResolver The function to resolve the claim.
   * @return The resolved claim.
   */
  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  /**
   * Checks if the provided JWT token is valid for the given UserDetails.
   *
   * @param token The JWT token.
   * @param userDetails The UserDetails object containing user details.
   * @return True if the token is valid, false otherwise.
   */
  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && isTokenExpired(token);
  }

  /**
   * Checks if the provided JWT token is expired.
   *
   * @param token The JWT token.
   * @return True if the token is expired, false otherwise.
   */
  @Override
  public boolean isTokenExpired(String token) {

    return extractExpiration(token).before(new Date());
  }

  /**
   * Extracts the expiration date from the provided JWT token.
   *
   * @param token The JWT token.
   * @return The expiration date extracted from the token.
   */
  @Override
  public Date extractExpiration(String token) {

    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts all claims from the provided JWT token.
   *
   * @param token The JWT token.
   * @return All claims extracted from the token.
   */
  @Override
  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Retrieves the signing key used for JWT token verification.
   *
   * @return The signing key as a Key object.
   */
  @Override
  public Key getSignInKey() {
    byte[] KeyBytes = Decoders.BASE64.decode(env.getProperty("jwt.secret-key"));
    return Keys.hmacShaKeyFor(KeyBytes);
  }
}
