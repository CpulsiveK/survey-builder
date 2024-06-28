package com.amalitech.surveysphere.dto.responseDto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AiBlock implements Serializable {
    private String title;
    private List<AiQuestion> questions;
}
