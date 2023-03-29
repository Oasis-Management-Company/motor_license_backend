package com.app.IVAS.dto;

import com.app.IVAS.Enum.CardTypeConstant;
import lombok.Data;

@Data
public class PrintDto {
    private Long id;
    private CardTypeConstant type;
}
