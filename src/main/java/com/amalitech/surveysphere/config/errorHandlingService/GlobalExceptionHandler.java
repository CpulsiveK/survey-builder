package com.amalitech.surveysphere.config.errorHandlingService;

import com.amalitech.surveysphere.dto.responseDto.ErrorResponseDto;
import com.amalitech.surveysphere.exceptions.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Global exception handler for handling various exceptions and providing appropriate error
 * responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles IllegalArgumentException and returns a BAD_REQUEST response.
   *
   * @param exception The IllegalArgumentException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and BAD_REQUEST
   *     status.
   */
  @ExceptionHandler(value = {IllegalArgumentException.class})
  ResponseEntity<ErrorResponseDto> handleBadRequest(
      IllegalArgumentException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles NotFoundException and returns a NOT_FOUND response.
   *
   * @param exception The NotFoundException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and NOT_FOUND
   *     status.
   */
  @ExceptionHandler(value = {NotFoundException.class})
  ResponseEntity<ErrorResponseDto> handleNotFound(
      NotFoundException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.NOT_FOUND);
  }

  /**
   * Handles DuplicateException and returns a CONFLICT response.
   *
   * @param exception The DuplicateException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and CONFLICT
   *     status.
   */
  @ExceptionHandler(value = {DuplicateException.class})
  ResponseEntity<ErrorResponseDto> handleInternalServerError(
      DuplicateException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.CONFLICT);
  }

  /**
   * Handles DataAccessException and returns an INTERNAL_SERVER_ERROR response.
   *
   * @param exception The DataAccessException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and
   *     INTERNAL_SERVER_ERROR status.
   */
  @ExceptionHandler(value = {DataAccessException.class})
  ResponseEntity<ErrorResponseDto> handleDataAccess(
      DataAccessException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * Handles UnauthorizedException and returns an UNAUTHORIZED response.
   *
   * @param exception The UnauthorizedException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and UNAUTHORIZED
   *     status.
   */
  @ExceptionHandler(value = {UnauthorizedException.class})
  ResponseEntity<ErrorResponseDto> handleUnauthorized(
      UnauthorizedException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.UNAUTHORIZED);
  }

  /**
   * Handles ForbiddenException and returns a FORBIDDEN response.
   *
   * @param exception The ForbiddenException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and FORBIDDEN
   *     status.
   */
  @ExceptionHandler(value = {ForbiddenException.class})
  ResponseEntity<ErrorResponseDto> handleForbidden(
      ForbiddenException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.FORBIDDEN);
  }

  /**
   * Handle exceptions of type MailException.
   *
   * @param exception The MailException instance that was thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with details of the error.
   */
  @ExceptionHandler(value = {MessagingException.class})
  ResponseEntity<ErrorResponseDto> handleMaessagingException(
      MessagingException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles parse exceptions.
   *
   * @param exception The ParseException instance that was thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with details of the error and HTTP
   *     status code 400 (Bad Request).
   */
  @ExceptionHandler(value = {ParseException.class})
  ResponseEntity<ErrorResponseDto> handleParseException(
      ParseException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles IOExceptions.
   *
   * @param exception The IOException that occurred.
   * @param httpServletRequest The HTTP servlet request where the exception happened.
   * @return A ResponseEntity containing an ErrorResponseDto and HTTP status code.
   */
  @ExceptionHandler(value = {IOException.class})
  ResponseEntity<ErrorResponseDto> handleIOException(
      IOException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles BindException and returns a BAD_REQUEST response.
   *
   * @param exception The BindException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and
   *     UNPROCESSABLE_ENTITY status.
   */
  @ExceptionHandler(value = {BindException.class})
  ResponseEntity<ErrorResponseDto> handleValidation(
      BindException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(
            httpServletRequest.getRequestURI(),
            Objects.requireNonNull(exception.getBindingResult().getFieldError())
                .getDefaultMessage()),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }

  /**
   * Handles MissingServletRequestParameterException and returns a BAD_REQUEST response.
   *
   * @param exception The MissingServletRequestParameterException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and BAD_REQUEST
   *     status.
   */
  @ExceptionHandler(value = {MissingServletRequestParameterException.class})
  ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles HttpClientErrorException and returns a BAD_REQUEST response.
   *
   * @param exception The HttpClientErrorException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and BAD_REQUEST
   *     status.
   */
  @ExceptionHandler(value = {HttpClientErrorException.class})
  ResponseEntity<ErrorResponseDto> handleHttpClientErrorException(
      HttpClientErrorException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles RestClientException and returns a BAD_REQUEST response.
   *
   * @param exception The RestClientException thrown.
   * @param httpServletRequest The HttpServletRequest associated with the request.
   * @return A ResponseEntity containing an ErrorResponseDto with the error details and BAD_REQUEST
   *     status.
   */
  @ExceptionHandler(value = {RestClientException.class})
  ResponseEntity<ErrorResponseDto> handleRestClientException(
      RestClientException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * This method handles exceptions of type AiException. It returns a ResponseEntity with an
   * ErrorResponseDto containing details about the error, such as the request URI and the exception
   * message. The HTTP status code returned is HttpStatus.TOO_MANY_REQUESTS (429).
   *
   * @param exception The AiException that was thrown.
   * @param httpServletRequest The HttpServletRequest object containing the request information.
   * @return A ResponseEntity containing an ErrorResponseDto with error details and the appropriate
   *     HTTP status code.
   */
  @ExceptionHandler(value = {AiException.class})
  ResponseEntity<ErrorResponseDto> handleAiException(
      AiException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(value = {WebClientException.class})
  ResponseEntity<ErrorResponseDto> handleAccessTokenException(
      WebClientException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(value = {DeactivatedException.class})
  ResponseEntity<ErrorResponseDto> handleDeactivatedException(
      DeactivatedException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = {DeletedException.class})
  ResponseEntity<ErrorResponseDto> handleDeletedException(
      DeletedException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.GONE);
  }

  @ExceptionHandler(value = {WebClientResponseException.class})
  ResponseEntity<ErrorResponseDto> handleWebClientResponseException(
      WebClientResponseException exception, HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(
        new ErrorResponseDto(httpServletRequest.getRequestURI(), exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }
}
