package com.amalitech.surveysphere.dto.responseDto;

import com.amalitech.surveysphere.models.Survey;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllSurveysResponseDto {
    private List<Survey> surveys;
    private int totalPages;
}
