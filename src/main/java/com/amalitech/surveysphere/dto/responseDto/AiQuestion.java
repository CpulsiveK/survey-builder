package com.amalitech.surveysphere.dto.responseDto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AiQuestion implements Serializable {
    private String content;
    private List<String> options;
    private String type;
}
