package com.amalitech.surveysphere.config;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.*;

import com.amalitech.surveysphere.config.authenticationProvider.EmailAuthenticationProvider;
import com.amalitech.surveysphere.config.errorHandlingService.AccessDeniedExceptionHandler;
import com.amalitech.surveysphere.dto.responseDto.LogoutResponseDto;
import com.amalitech.surveysphere.dto.responseDto.UserFailureDto;
import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.UserRepository;
import com.amalitech.surveysphere.services.otherServices.userDetailsService.IUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;

/** Configuration class for defining security configurations. */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final IUserDetailsService iUserDetailsService;
  private final UserRepository userRepository;
  private final Environment env;
  private static final String CONTENT_TYPE = "application/json";
  private final EmailAuthenticationProvider emailAuthenticationProvider;

  private static final String[] PUBLIC_URLS = {
    "/survey-sphere/public/**",
    "/v2/api-docs",
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/swagger-resources",
    "/swagger-resources/**",
    "/configuration/ui",
    "/configuration/security",
    "/swagger-ui/**",
    "/webjars/**",
    "/swagger-ui.html"
  };

  /**
   * Bean definition for custom AccessDeniedHandler.
   *
   * @return An instance of AccessDeniedHandler
   */
  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new AccessDeniedExceptionHandler();
  }

  /**
   * Configures the security settings for the application.
   *
   * @param http HttpSecurity object
   * @return A SecurityFilterChain instance
   * @throws Exception Thrown if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.exceptionHandling(
            exceptionHandling -> exceptionHandling.accessDeniedHandler(accessDeniedHandler()))
        .cors(
            cors ->
                cors.configurationSource(
                    request -> {
                      CorsConfiguration configuration = new CorsConfiguration();
                      configuration.setAllowedOrigins(
                          List.of(Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN"))));
                      configuration.setAllowedMethods(
                          List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
                      configuration.setAllowedHeaders(
                          List.of("Content-Type", "Content-Disposition", "Authorization"));
                      configuration.setAllowCredentials(true);
                      return configuration;
                    }))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            request ->
                request
                    .requestMatchers(PUBLIC_URLS)
                    .permitAll()
                    .requestMatchers("/survey-sphere/user/**")
                    .hasAnyAuthority("USER", "ADMIN", "TEAM", "BUSINESS")
                    .requestMatchers("/survey-sphere/admin/**")
                    .hasAnyAuthority("ADMIN")
                    .anyRequest()
                    .authenticated())
        .formLogin(
            form ->
                form.loginProcessingUrl("/survey-sphere/public/login")
                    .loginPage(
                        Objects.requireNonNull(env.getProperty("FRONTEND_ORIGIN")).concat("/login"))
                    .usernameParameter("email")
                    .successHandler(this::authenticationSuccessHandler)
                    .failureHandler(this::authenticationFailureHandler)
                    .permitAll())
        .logout(
            logout ->
                logout
                    .logoutUrl("/survey-sphere/public/logout")
                    .logoutSuccessHandler(this::logoutSuccessHandler)
                    .permitAll())
        .sessionManagement(
            sessionManagement ->
                sessionManagement.maximumSessions(1).sessionRegistry(sessionRegistry()))
        .build();
  }

  private void logoutSuccessHandler(
      HttpServletRequest request,
      HttpServletResponse httpServletResponse,
      Authentication authentication)
      throws IOException {
    httpServletResponse.setContentType(CONTENT_TYPE);
    new ObjectMapper()
        .writeValue(
            httpServletResponse.getOutputStream(),
            LogoutResponseDto.builder().message("You have been logged out").build());

    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(iUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return new ProviderManager(
        Arrays.asList(emailAuthenticationProvider, authenticationProvider()));
  }

  /** Handles successful authentication. */
  private void authenticationSuccessHandler(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    response.setContentType(CONTENT_TYPE);
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    String email = userDetails.getUsername();

    Optional<User> userExists = userRepository.findByEmail(email);

    if (userExists.isEmpty()) throw new UsernameNotFoundException(USER_NOT_FOUND.getMessage());

    UserResponseDto responseDto =
        UserResponseDto.builder()
            .id(userExists.get().getId())
            .profilePicture(userExists.get().getProfilePicture())
            .username(userExists.get().getUsername())
            .email(userExists.get().getEmail())
            .socialProviders(userExists.get().getSocialLogins())
            .role(userExists.get().getRole())
            .isAccountEnabled(userExists.get().isEnabled())
            .aiCount(userExists.get().getAiCount())
            .subscriptionCode(userExists.get().getSubscriptionCode())
            .build();

    new ObjectMapper().writeValue(response.getOutputStream(), responseDto);

    HttpSession session = request.getSession();

    session.setMaxInactiveInterval((int) TimeUnit.DAYS.toSeconds(1));

    sessionRegistry().registerNewSession(session.getId(), authentication.getPrincipal());

    response.setStatus(HttpServletResponse.SC_OK);
  }

  /** Handles authentication failure. */
  private void authenticationFailureHandler(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.setContentType(CONTENT_TYPE);
    new ObjectMapper()
        .writeValue(
            response.getOutputStream(),
            new UserFailureDto("Bad credentials", request.getRequestURI()));
  }

  /**
   * Bean definition for SessionRegistry.
   *
   * @return An instance of SessionRegistry
   */

  /**
   * Bean definition for HttpSessionEventPublisher.
   *
   * @return An instance of HttpSessionEventPublisher
   */
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }
}
