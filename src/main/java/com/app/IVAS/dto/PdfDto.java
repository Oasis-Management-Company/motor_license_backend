package com.app.IVAS.dto;

import com.app.IVAS.entity.Card;
import lombok.Data;

import java.util.Map;

@Data
public class PdfDto {
    private String templateName;
    private Card card;
    private Map<String, Object> extraParameter;
}
