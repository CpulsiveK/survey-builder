package com.amalitech.surveysphere.services.questionService;

import static com.amalitech.surveysphere.enums.CustomExceptionMessage.QUESTION_NOT_FOUND;

import com.amalitech.surveysphere.exceptions.NotFoundException;
import com.amalitech.surveysphere.models.Block;
import com.amalitech.surveysphere.models.Question;
import com.amalitech.surveysphere.repositories.BlockRespository;
import com.amalitech.surveysphere.repositories.QuestionRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImplementation implements QuestionService {
  private final BlockRespository blockRespository;
  private final QuestionRepository questionRepository;

  /**
   * Saves all responses, questions and blocks
   *
   * @param blocks The list of blocks.
   * @return List of blocks.
   */
  @Override
  public List<Block> saveBlocks(List<Block> blocks) {
    return blocks.stream()
        .map(
            block -> {
              List<Question> questionsToSave =
                  block.getQuestions().stream().peek(question -> question.setId(null)).toList();

              questionRepository.saveAll(questionsToSave);
              block.setId(null);
              return blockRespository.save(block);
            })
        .toList();
  }

  /**
   * updates all responses, questions and blocks
   *
   * @param blocks The list of blocks.
   * @return List of blocks.
   */
  @Override
  public List<Block> updateBlocks(List<Block> blocks) {
    return blocks.stream()
        .map(
            block -> {
              if (block.getId() == null || block.getId().isBlank()) {
                List<Question> questions = questionRepository.saveAll(block.getQuestions());
                block.setQuestions(questions);
              } else {
                saveQuestionsWithIds(block);
                saveQuestionsWithoutIds(block);
                block.setQuestions(block.getQuestions());
              }
              return blockRespository.save(block);
            })
        .toList();
  }

  private void saveQuestionsWithIds(Block block) {
    List<Question> questionsWithIds =
        block.getQuestions().stream().filter(question -> question.getId() != null).toList();

    questionRepository.saveAll(
        questionsWithIds.stream()
            .peek(
                question -> {
                  Optional<Question> questionExists = questionRepository.findById(question.getId());

                  if (questionExists.isEmpty())
                    throw new NotFoundException(QUESTION_NOT_FOUND.getMessage());

                  Question updateQuestion = questionExists.get();

                  updateQuestion.setTitle(question.getTitle());
                  updateQuestion.setType(question.getType());
                  updateQuestion.setOptions(question.getOptions());
                  updateQuestion.setConditions(question.getConditions());
                })
            .toList());
  }

  private void saveQuestionsWithoutIds(Block block) {
    List<Question> questionsWithoutIds =
        block.getQuestions().stream()
            .filter(question -> question.getId() == null || question.getId().isBlank())
            .toList();

    List<Question> savedQuestions = questionRepository.saveAll(questionsWithoutIds);

    Map<Question, Question> savedQuestionMap =
        savedQuestions.stream().collect(Collectors.toMap(q -> q, Function.identity()));

    questionsWithoutIds.forEach(
        question -> {
          Question savedQuestion = savedQuestionMap.get(question);
          if (savedQuestion != null) {
            int index = block.getQuestions().indexOf(question);
            block.getQuestions().set(index, savedQuestion);
          }
        });
  }
}
