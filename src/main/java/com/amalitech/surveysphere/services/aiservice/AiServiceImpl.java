package com.amalitech.surveysphere.services.aiservice;

import static com.amalitech.surveysphere.enums.Endpoint.*;
import static org.mvnsearch.chatgpt.model.function.GPTFunctionUtils.objectMapper;

import com.amalitech.surveysphere.dto.requestDto.AiRequest;
import com.amalitech.surveysphere.dto.requestDto.FileUploadDto;
import com.amalitech.surveysphere.dto.responseDto.AiSurveyDto;
import com.amalitech.surveysphere.exceptions.AiException;
import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.models.*;
import com.amalitech.surveysphere.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
  private final UserRepository userRepository;

  private static final String TOKEN ="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxMzA2LCJlbWFpbCI6ImVtbWFudWVsLm9tYXJpQGFtYWxpdGVjaC5jb20iLCJmaXJzdF9uYW1lIjoiRW1tYW51ZWwiLCJsYXN0X25hbWUiOiJPbWFyaSIsIm90aGVyX25hbWUiOm51bGwsIm9mZmljZSI6IjMyIiwib3JnYW5pemF0aW9uIjoiMzM3IiwicHJvZmlsZV9pbWFnZSI6bnVsbCwicm9sZXMiOltdLCJhcHBzIjpbImVtcGxveWVlIG1hbmFnZW1lbnQiLCJmJmMiLCJsb2FucyIsImxlYXZlIG1hbmFnZW1lbnQiLCJtZWV0aW5nIGJvb2tpbmciLCJwZXJmb3JtYW5jZSBtYW5hZ2VtZW50Il0sImlhdCI6MTcxNjU0MDk3NiwiZXhwIjoxNzE2NTc2OTc2LCJhdWQiOiJodHRwczovL2FtYWxpdGVjaC5jb20vIiwiaXNzIjoiQW1hbGl0ZWNoIFNTTyIsInN1YiI6ImluZm9AYW1hbGl0ZWNoLmNvbSJ9.ZUsPO98fRopj716wnb2Gq7FnzLTHyW_Ba8k8WmOmM5KEAGRwckXJfCg9ojOASkSXz3JlczRkvlKwJqvVYa60A-hwI0Mo3-GxYqx8NfGNOCxHKvtErEYj5Pq6k3fio7TuzNAHJoXG7f6zEvZt3_nOeMh_L7Y_gnAQF-gHiTjiV3No6fs9nbygqXgRvuT3-qss9EOK8nAy5fHD6AZrCWAFUZlp01Mq_h6fAj4PySQmqD1RaQVaw9nu6roVynZL4av7lYalgJdekM_idjQ4qYel4Cn8zv7W024uuvXfaUMOl_QPwBY5LACqeH_ZZwpT0Nz5XaM9BsDhzzwNy57Euz9RUw";
          WebClient webClient =
      WebClient.builder()
          .baseUrl(AI_API_URL.getUrl())
          .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
          .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
          .clientConnector(
              new ReactorClientHttpConnector(
                  HttpClient.newConnection()
                      .compress(true)
                      .responseTimeout(Duration.ofMinutes(10))))
          .build();

  @Override
  public Mono<Object> createSurveysAI(
      String title,
      String category,
      String description,
      String blocks,
      String targetAudience,
      String questions,
      UserDetails userDetails) {

    Optional<User> findUser = userRepository.findByEmail(userDetails.getUsername());
    if (findUser.isPresent()) {
      if (findUser.get().getAiCount() == 5 && !(findUser.get().getRole().equals("BUSINESS"))) {
        throw new AiException("Limit Exceeded");
      }
      findUser.get().setAiCount(findUser.get().getAiCount() + 1);
      userRepository.save(findUser.get());
    } else {
      throw new NotFoundException("User Not Found");
    }
    try {
      AiRequest requestBody =
          createAiRequest(title, category, description, blocks, targetAudience, questions);

      return webClient
          .post()
          .body(BodyInserters.fromValue(requestBody))
          .retrieve()
          .bodyToFlux(Object.class)
          .last()
          .map(
              responseObject -> {
                String content = parseContent(responseObject);
                return mapContentToAiSurveyDto(content);
              })
          .map(
              aiSurveyDto -> {
                return Survey.builder()
                    .colorScheme(
                        ColorScheme.builder().backgroundColor("#F7F9FC").textColor("black").build())
                    .logo(
                        FileUploadDto.builder()
                            .url(
                                "https://survey-sphere-bucket.s3.amazonaws.com/$2a$10$DBIEZ2/2L7QL0d9my/yxjuSRLr0NL2M4Ya.Yg5KyJr1JGWMM4KW1i")
                            .style(Style.builder().alignment("left").build())
                            .build())
                    .surveyTitle(
                        SurveyTitle.builder()
                            .title(
                                Title.builder()
                                    .titleName(aiSurveyDto.getTitle())
                                    .style(
                                        SettingsStyle.builder()
                                            .bold(false)
                                            .underline(false)
                                            .strikethrough(false)
                                            .italic(false)
                                            .small(false)
                                            .build())
                                    .build())
                            .description(
                                Description.builder()
                                    .style(
                                        SettingsStyle.builder()
                                            .bold(false)
                                            .underline(false)
                                            .strikethrough(false)
                                            .italic(false)
                                            .small(false)
                                            .build())
                                    .detail(aiSurveyDto.getDescription())
                                    .build())
                            .build())
                    .category(aiSurveyDto.getCategory())
                    .blocks(
                        aiSurveyDto.getBlocks().stream()
                            .map(
                                block ->
                                    Block.builder()
                                        .title(
                                            Title.builder()
                                                .titleName(block.getTitle())
                                                .style(
                                                    SettingsStyle.builder()
                                                        .bold(false)
                                                        .underline(false)
                                                        .strikethrough(false)
                                                        .italic(false)
                                                        .small(false)
                                                        .build())
                                                .build())
                                        .questions(
                                            block.getQuestions().stream()
                                                .map(
                                                    question ->
                                                        Question.builder()
                                                            .conditions(
                                                                Condition.builder()
                                                                    .blockIndex(0)
                                                                    .answer("")
                                                                    .choice("")
                                                                    .question("")
                                                                    .type("")
                                                                    .answer("")
                                                                    .conditionalOptions(
                                                                        new ArrayList<>())
                                                                    .build())
                                                            .title(
                                                                QuestionTitle.builder()
                                                                    .style(
                                                                        SettingsStyle.builder()
                                                                            .bold(false)
                                                                            .underline(false)
                                                                            .strikethrough(false)
                                                                            .italic(false)
                                                                            .small(false)
                                                                            .build())
                                                                    .question(question.getContent())
                                                                    .build())
                                                            .type(question.getType())
                                                            .options(question.getOptions())
                                                            .build())
                                                .collect(Collectors.toList()))
                                        .build())
                            .collect(Collectors.toList()))
                    .build();
              });
    } catch (Exception e) {
      throw new WebClientException("Amali-Ai is currently unavailable...Please try Again Later") {};
    }
  }

  private AiRequest createAiRequest(
      String title,
      String category,
      String description,
      String blocks,
      String targetAudience,
      String questions) {
    String requestBody =
        "respond with only this json format;{title: \"title\",description: \"description\", category: \"category\", blocks:[{title:\"title\",questions:[{content:\"question-content\",type:\"question-type\",options:[\"option1\",\"option2\"], }].question type should only be short-text, multiple-choice, single-choice, dropdown and paragraph.use descriptive block names. create a"
            + title
            + " survey with a total of +"
            + questions
            + " questions spread across "
            + blocks
            + " block(s), with "
            + category
            + " category and description: "
            + description
            + " for a "
            + targetAudience
            + " audience.";
    return new AiRequest(requestBody);
  }

  private String parseContent(Object responseObject) {
    String content = null;
    if (responseObject instanceof Map<?, ?> responseMap) {
      Object contentObj = responseMap.get("content");
      if (contentObj instanceof String) {
        content = (String) contentObj;
      }
    }
    return content;
  }

  private AiSurveyDto mapContentToAiSurveyDto(String content) {
    String ss = content.substring(7, content.length() - 3);
    try {
      return objectMapper.readValue(ss, AiSurveyDto.class);
    } catch (JsonProcessingException e) {
      throw new AiException("Error parsing AI response... Try again later.");
    }
  }
}
