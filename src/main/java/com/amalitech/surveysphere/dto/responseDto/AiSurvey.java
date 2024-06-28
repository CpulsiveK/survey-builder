package com.amalitech.surveysphere.dto.responseDto;

import com.amalitech.surveysphere.models.Block;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiSurvey {
    private String title;
    private String description;
    private String category;
    private List<Block> blocks;
}
