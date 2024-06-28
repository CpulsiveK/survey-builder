package com.amalitech.surveysphere.services.socialUserService.socialProviderverificationService;

import com.amalitech.surveysphere.dto.requestDto.SocialLoginDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Implementation class for verifying JWT tokens. */
@Service
public class JwtVerificationServiceImplementation implements JwtVerificatonService {

  @Value("${GOOGLE_CLIENT_ID}")
  private String GOOGLE_CLIENT_ID;

  private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private final GoogleIdTokenVerifier verifier;

  /** Constructor initializing the GoogleIdTokenVerifier. */
  public JwtVerificationServiceImplementation() {
    this.verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JSON_FACTORY)
            .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
            .build();
  }

  /**
   * Decodes the JWT token and retrieves user information.
   *
   * @param jwt The JWT token to be decoded.
   * @return The SocialLoginDto object containing the decoded user information.
   * @throws ParseException If an error occurs during JWT verification and decoding.
   */
  @Override
  public SocialLoginDto decodeJwt(String jwt) throws ParseException {
    JWTClaimsSet jwtClaimsSet = JWTParser.parse(jwt).getJWTClaimsSet();
    String email = jwtClaimsSet.getStringClaim("email");
    String givenName = jwtClaimsSet.getStringClaim("given_name ");
    String familyName = jwtClaimsSet.getStringClaim("family_name");
    String name = givenName + familyName;
    String username = jwtClaimsSet.getStringClaim("name");

    return new SocialLoginDto(name, username, email, null, "GOOGLE");
  }
}
