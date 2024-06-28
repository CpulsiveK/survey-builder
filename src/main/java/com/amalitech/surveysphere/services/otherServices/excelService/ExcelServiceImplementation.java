package com.amalitech.surveysphere.services.otherServices.excelService;

import com.amalitech.surveysphere.dto.responseDto.ResponseAnalysisDto;
import com.amalitech.surveysphere.services.responseManagementService.ResponseManagementService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcelServiceImplementation implements ExcelService {
  private final ResponseManagementService responseManagementService;

  @Override
  public byte[] generateExcelFiles(String surveyId) throws IOException {
    ResponseAnalysisDto surveyResponse = responseManagementService.getResponses(surveyId);
    try {
      JSONObject jsonObject = new JSONObject(surveyResponse);
      List<String> questions = extractQuestions(jsonObject.getJSONArray("responses"));
      List<List<String>> answersLists =
          processRespondentAnswers(jsonObject.getJSONArray("individualResults"), questions);

      return createExcelFile(questions, answersLists);
    } catch (JSONException | IOException e) {
      throw new IOException("Error generating Excel file", e);
    }
  }

  private List<String> extractQuestions(JSONArray responsesArray) {
    List<String> questions = new ArrayList<>();
    for (int i = 0; i < responsesArray.length(); i++) {
      JSONObject responseObj = responsesArray.getJSONObject(i);
      String question = responseObj.getString("question");
      questions.add(question);
    }
    return questions;
  }

  private List<List<String>> processRespondentAnswers(
      JSONArray individualResultsArray, List<String> questions) {
    List<List<String>> answersLists = new ArrayList<>();
    for (int i = 0; i < individualResultsArray.length(); i++) {
      JSONObject respondentObj = individualResultsArray.getJSONObject(i);
      JSONArray respondentResponses = respondentObj.getJSONArray("responses");
      List<String> answers = initializeAnswers(questions);
      updateAnswers(respondentResponses, questions, answers);
      answersLists.add(answers);
    }
    return answersLists;
  }

  private List<String> initializeAnswers(List<String> questions) {
    return new ArrayList<>(Collections.nCopies(questions.size(), "-"));
  }

  private void updateAnswers(
      JSONArray respondentResponses, List<String> questions, List<String> answers) {
    for (int j = 0; j < respondentResponses.length(); j++) {
      JSONObject responseObj = respondentResponses.getJSONObject(j);
      String question = responseObj.getString("question");
      int questionIndex = questions.indexOf(question);
      JSONArray answerArray = responseObj.getJSONArray("answer");

      String answer = buildAnswer(answerArray);
      if (questionIndex != -1) {
        answers.set(questionIndex, answer.isEmpty() ? "Skipped" : answer);
      }
    }
  }

  private String buildAnswer(JSONArray answerArray) {
    StringBuilder answerBuilder = new StringBuilder();
    for (int k = 0; k < answerArray.length(); k++) {
      answerBuilder.append(answerArray.getString(k));
      if (k < answerArray.length() - 1) {
        answerBuilder.append(", ");
      }
    }
    return answerBuilder.toString();
  }

  private byte[] createExcelFile(List<String> questions, List<List<String>> answersLists)
      throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Survey Responses");

      // Create header row with questions as column headers
      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < questions.size(); i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(questions.get(i));
      }

      // Fill data with answers under respective questions
      int rowIndex = 1;
      for (List<String> answers : answersLists) {
        Row row = sheet.createRow(rowIndex++);
        for (int i = 0; i < answers.size(); i++) {
          Cell cell = row.createCell(i);
          cell.setCellValue(answers.get(i));
        }
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);

      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new IOException("Error generating Excel file", e);
    }
  }
    }
