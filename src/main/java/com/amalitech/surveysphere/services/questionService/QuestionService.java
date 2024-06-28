package com.amalitech.surveysphere.services.questionService;

import com.amalitech.surveysphere.models.Block;

import java.util.List;

public interface QuestionService {
    List<Block> saveBlocks(List<Block> blocks);

    List<Block> updateBlocks(List<Block> blocks);
}
