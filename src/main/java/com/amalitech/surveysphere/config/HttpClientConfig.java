package com.amalitech.surveysphere.config;

import static com.amalitech.surveysphere.enums.Endpoint.*;

import com.amalitech.surveysphere.client.PaystackClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {
  @Bean
  public WebClient paystackClient() {
    return WebClient.builder().baseUrl(PAYSTACK_BASE_URL.getUrl()).build();
  }

  @Bean
  PaystackClient paymentService() {
    HttpServiceProxyFactory factory =
            HttpServiceProxyFactory.builderFor(WebClientAdapter.create(paystackClient())).build();
    return factory.createClient(PaystackClient.class);
  }

  @Bean
  HttpHeaders httpHeaders() {
    return new HttpHeaders();
  }
}
