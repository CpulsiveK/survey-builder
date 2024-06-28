package com.amalitech.surveysphere.services.paymentService;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.*;
import static com.amalitech.surveysphere.enums.Plan.*;

import com.amalitech.surveysphere.client.PaystackClient;
import com.amalitech.surveysphere.dto.paystackDto.*;
import com.amalitech.surveysphere.dto.paystackDto.Subscription;
import com.amalitech.surveysphere.dto.requestDto.PaymentRequestDto;
import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.models.User;
import com.amalitech.surveysphere.repositories.UserRepository;
import com.amalitech.surveysphere.services.responseService.UserResponseService;
import com.amalitech.surveysphere.services.surveyService.SurveyService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImplementation implements PaymentService {
  private final Environment env;
  private final UserRepository userRepository;
  private final SurveyService surveyService;
  private final UserResponseService userResponseService;
  private final PaystackClient paystackClient;

  Map<String, String> headers = new HashMap<>();

  /**
   * Handles charge authorization for the user and subscribes the user to the subscription plan
   *
   * @param paymentRequestDto
   * @return
   * @throws RestClientException
   */
  @Override
  public Object initializePayment(PaymentRequestDto paymentRequestDto, String subscriptionCode)
      throws RestClientException {
    if (subscriptionCode == null) {
      return paystackClient.initializePayment(getApiKey(), paymentRequestDto);
    }
    Subscription subscription = paystackClient.fetchSubscription(getApiKey(), subscriptionCode);

    if (Objects.equals(subscription.getSubscription_code(), subscriptionCode))
      throw new RestClientException("You are already on subscription");

    return paystackClient.initializePayment(getApiKey(), paymentRequestDto);
  }

  /**
   * Verifies the charge authorization and subscription and updates user role
   *
   * @param reference
   * @param planCode
   * @param httpServletRequest
   * @return UserResponseDto
   * @throws RestClientException
   * @throws IOException
   */
  @Override
  public UserResponseDto verifyPayment(
      String reference,
      String planCode,
      String subscriptionCode,
      HttpServletRequest httpServletRequest)
      throws RestClientException, IOException {

    PaymentResponse paymentResponse = paystackClient.verifyPayment(getApiKey(), reference);

    if (subscriptionCode != null) {
      disableSubscription(subscriptionCode);
    }

    SubscriptionResponse subscriptionResponse =
        paystackClient.listSubscription(
            getApiKey(), paymentResponse.getData().getCustomer().getId());

    Data data = subscriptionResponse.getData().getFirst();

    User user =
        userRepository
            .findById(surveyService.getUserId())
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

    user.setRole(planCode.equals(TEAM.getPlan()) ? TEAM.name() : BUSINESS.name());
    user.setSubscriptionCode(data.getSubscription_code());
    userRepository.save(user);
    return userResponseService.sendSuccessResponse(user, httpServletRequest);
  }

  private void disableSubscription(String subscriptionCode) {
    Subscription subscription = paystackClient.fetchSubscription(getApiKey(), subscriptionCode);

    paystackClient.disableSubscription(
        getApiKey(),
        DisableSubscriptionRequestDto.builder()
            .code(subscriptionCode)
            .token(subscription.getEmail_token())
            .build());
  }

  private Map<String, String> getApiKey() {
    headers.put(
        "Authorization", "Bearer " + Objects.requireNonNull(env.getProperty("paystack_secret")));
    return headers;
  }
}
